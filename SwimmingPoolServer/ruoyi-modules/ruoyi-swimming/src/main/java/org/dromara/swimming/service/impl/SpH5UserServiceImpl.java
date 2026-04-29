package org.dromara.swimming.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.swimming.domain.SpMemberCard;
import org.dromara.swimming.domain.SpReservation;
import org.dromara.swimming.domain.vo.SpH5UserStatisticsVo;
import org.dromara.swimming.mapper.SpMemberCardMapper;
import org.dromara.swimming.mapper.SpReservationMapper;
import org.dromara.swimming.service.ISpH5UserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * H5用户服务实现
 *
 * @author swimming
 * @date 2026-02-04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SpH5UserServiceImpl implements ISpH5UserService {

    private final SpReservationMapper reservationMapper;
    private final SpMemberCardMapper memberCardMapper;

    // 预约状态常量
    private static final String RESERVATION_STATUS_PAID = "1";
    private static final String RESERVATION_STATUS_COMPLETED = "2";

    @Override
    public R<SpH5UserStatisticsVo> getUserStatistics() {
        try {
            Long userId = LoginHelper.getUserId();

            // 1. 查询预约次数（已支付或已完成的预约）
            LambdaQueryWrapper<SpReservation> reservationWrapper = new LambdaQueryWrapper<>();
            reservationWrapper.eq(SpReservation::getUserId, userId);
            reservationWrapper.and(w -> w.eq(SpReservation::getStatus, RESERVATION_STATUS_PAID)
                .or()
                .eq(SpReservation::getStatus, RESERVATION_STATUS_COMPLETED));

            List<SpReservation> reservations = reservationMapper.selectList(reservationWrapper);
            int reservationCount = reservations.size();

            // 2. 计算游泳时长（根据预约的开始和结束时间）
            long totalMinutes = 0;
            for (SpReservation reservation : reservations) {
                if (reservation.getStartTime() != null && reservation.getEndTime() != null) {
                    long startMinutes = reservation.getStartTime().getTime();
                    long endMinutes = reservation.getEndTime().getTime();
                    long minutes = (endMinutes - startMinutes) / (1000 * 60);
                    totalMinutes += minutes;
                }
            }
            double totalHours = Math.round(totalMinutes / 60.0 * 10) / 10.0; // 保留1位小数

            // 3. 查询账户余额和积分
            LambdaQueryWrapper<SpMemberCard> cardWrapper = new LambdaQueryWrapper<>();
            cardWrapper.eq(SpMemberCard::getUserId, userId);
            SpMemberCard memberCard = memberCardMapper.selectOne(cardWrapper);

            long balance = 0L;
            long totalPoints = 0L;
            if (memberCard != null) {
                balance = memberCard.getBalance() != null ? memberCard.getBalance() : 0L;
                totalPoints = memberCard.getTotalPoint() != null ? memberCard.getTotalPoint() : 0L;
            }

            // 封装结果
            SpH5UserStatisticsVo vo = new SpH5UserStatisticsVo();
            vo.setReservationCount(reservationCount);
            vo.setTotalHours(totalHours);
            vo.setBalance(balance);
            vo.setTotalPoints(totalPoints);

            return R.ok(vo);
        } catch (Exception e) {
            log.error("获取用户统计数据失败", e);
            return R.fail("获取统计数据失败");
        }
    }
}
