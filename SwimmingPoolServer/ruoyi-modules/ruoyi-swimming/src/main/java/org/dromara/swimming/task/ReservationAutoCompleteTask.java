package org.dromara.swimming.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.swimming.domain.SpReservation;
import org.dromara.swimming.domain.SpTimeSlot;
import org.dromara.swimming.mapper.SpOrderMapper;
import org.dromara.swimming.mapper.SpReservationMapper;
import org.dromara.swimming.mapper.SpTimeSlotMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 预约自动完成定时任务
 *
 * @author swimming
 * @date 2026-02-04
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationAutoCompleteTask {

    private final SpReservationMapper reservationMapper;
    private final SpTimeSlotMapper timeSlotMapper;
    private final SpOrderMapper orderMapper;

    // 预约状态字典
    private static final String RESERVATION_STATUS_PAID = "1";
    private static final String RESERVATION_STATUS_COMPLETED = "2";

    // 订单状态字典
    private static final String ORDER_STATUS_PAID = "1";
    private static final String ORDER_STATUS_COMPLETED = "2";

    /**
     * 定时任务：每5分钟执行一次，自动完成已过期的预约
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void autoCompleteExpiredReservations() {
        try {
            log.info("开始执行预约自动完成定时任务");

            // 查询所有已支付但未完成的预约
            LambdaQueryWrapper<SpReservation> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SpReservation::getStatus, RESERVATION_STATUS_PAID);
            List<SpReservation> reservations = reservationMapper.selectList(wrapper);

            if (reservations == null || reservations.isEmpty()) {
                log.info("没有需要处理的预约记录");
                return;
            }

            int completedCount = 0;
            Date now = new Date();

            for (SpReservation reservation : reservations) {
                try {
                    // 查询时段信息
                    SpTimeSlot timeSlot = timeSlotMapper.selectById(reservation.getTimeSlotId());
                    if (timeSlot == null) {
                        log.warn("预约记录{}的时段信息不存在", reservation.getId());
                        continue;
                    }

                    // 构建时段结束时间
                    Date reservationDate = reservation.getReservationDate();
                    Date endTime = timeSlot.getEndTime();

                    // 合并日期和时间
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(reservationDate);
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);

                    calendar.setTime(endTime);
                    calendar.set(year, month, day);

                    Date slotEndDateTime = calendar.getTime();

                    // 检查当前时间是否已过时段结束时间
                    if (now.after(slotEndDateTime)) {
                        // 更新预约状态为已完成
                        reservation.setStatus(RESERVATION_STATUS_COMPLETED);
                        reservationMapper.updateById(reservation);

                        // 更新订单状态为已完成
                        updateOrderStatus(reservation.getId());

                        completedCount++;
                        log.info("自动完成预约：预约ID={}，预约单号={}，时段结束时间={}",
                            reservation.getId(), reservation.getReservationNo(), slotEndDateTime);
                    }
                } catch (Exception e) {
                    log.error("处理预约{}时发生错误", reservation.getId(), e);
                }
            }

            log.info("预约自动完成定时任务执行完成，共处理{}条记录", completedCount);
        } catch (Exception e) {
            log.error("预约自动完成定时任务执行失败", e);
        }
    }

    /**
     * 更新订单状态为已完成
     */
    private void updateOrderStatus(Long reservationId) {
        try {
            // 根据预约ID查询订单
            List<org.dromara.swimming.domain.SpOrder> orders = orderMapper.selectList(
                new LambdaQueryWrapper<org.dromara.swimming.domain.SpOrder>()
                    .eq(org.dromara.swimming.domain.SpOrder::getReservationId, reservationId)
                    .eq(org.dromara.swimming.domain.SpOrder::getStatus, ORDER_STATUS_PAID)
            );

            for (org.dromara.swimming.domain.SpOrder order : orders) {
                order.setStatus(ORDER_STATUS_COMPLETED);
                orderMapper.updateById(order);
                log.info("订单状态已更新为已完成：订单ID={}，订单号={}", order.getId(), order.getOrderNo());
            }
        } catch (Exception e) {
            log.error("更新订单状态失败，预约ID={}", reservationId, e);
        }
    }
}
