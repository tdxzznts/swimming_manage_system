package org.dromara.swimming.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.satoken.utils.LoginHelper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.dromara.swimming.domain.*;
import org.dromara.swimming.domain.vo.SpH5PaymentVo;
import org.dromara.swimming.mapper.*;
import org.dromara.swimming.service.ISpH5PaymentService;

/**
 * H5支付服务实现
 *
 * @author swimming
 * @date 2026-02-04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SpH5PaymentServiceImpl implements ISpH5PaymentService {

    private final SpOrderMapper orderMapper;
    private final SpMemberCardMapper memberCardMapper;
    private final SpMemberLevelMapper memberLevelMapper;
    private final SpMemberBenefitMapper memberBenefitMapper;
    private final SpReservationMapper reservationMapper;
    private final RedissonClient redissonClient;

    // 订单状态字典
    private static final String ORDER_STATUS_PENDING = "0";
    private static final String ORDER_STATUS_PAID = "1";
    private static final String ORDER_STATUS_COMPLETED = "2";

    // 支付状态字典
    private static final String PAY_STATUS_PENDING = "0";
    private static final String PAY_STATUS_SUCCESS = "1";

    // 预约状态字典
    private static final String RESERVATION_STATUS_PENDING = "0";
    private static final String RESERVATION_STATUS_PAID = "1";

    // 会员卡等级字典
    private static final String CARD_LEVEL_NORMAL = "0";
    private static final String CARD_LEVEL_SILVER = "1";
    private static final String CARD_LEVEL_GOLD = "2";
    private static final String CARD_LEVEL_DIAMOND = "3";

    // 分布式锁key前缀
    private static final String PAYMENT_LOCK_KEY = "payment:lock:";
    private static final long LOCK_WAIT_TIME = 3L;
    private static final long LOCK_LEASE_TIME = 10L;

    @Override
    public R<SpH5PaymentVo> getPaymentInfo(Long orderId) {
        Long userId = LoginHelper.getUserId();

        // 查询订单信息
        SpOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            return R.fail("订单不存在");
        }

        // 验证订单是否属于当前用户
        if (!order.getUserId().equals(userId)) {
            return R.fail("无权访问该订单");
        }

        // 查询会员卡信息
        SpMemberCard memberCard = memberCardMapper.selectOne(
            new LambdaQueryWrapper<SpMemberCard>()
                .eq(SpMemberCard::getUserId, userId)
                .eq(SpMemberCard::getStatus, "0")
        );

        if (memberCard == null) {
            return R.fail("会员卡不存在");
        }

        // 查询会员等级配置
        SpMemberLevel memberLevel = memberLevelMapper.selectOne(
            new LambdaQueryWrapper<SpMemberLevel>()
                .eq(SpMemberLevel::getLevelValue, Integer.parseInt(memberCard.getCardLevel()))
        );

        if (memberLevel == null) {
            return R.fail("会员等级配置不存在");
        }

        // 查询预约折扣权益
        SpMemberBenefit bookingDiscountBenefit = memberBenefitMapper.selectOne(
            new LambdaQueryWrapper<SpMemberBenefit>()
                .eq(SpMemberBenefit::getStatus, "0")
                .eq(SpMemberBenefit::getBenefitCode, "BOOKING_DISCOUNT")
                .eq(SpMemberBenefit::getLevelValue, Integer.parseInt(memberCard.getCardLevel()))
        );

        log.info("查询预约折扣权益 - cardLevel: {}, benefit: {}", memberCard.getCardLevel(), bookingDiscountBenefit);

        // 查询积分倍数权益
        SpMemberBenefit pointsDoubleBenefit = memberBenefitMapper.selectOne(
            new LambdaQueryWrapper<SpMemberBenefit>()
                .eq(SpMemberBenefit::getStatus, "0")
                .eq(SpMemberBenefit::getBenefitCode, "POINTS_DOUBLE")
                .eq(SpMemberBenefit::getLevelValue, Integer.parseInt(memberCard.getCardLevel()))
        );

        log.info("查询积分倍数权益 - cardLevel: {}, benefit: {}", memberCard.getCardLevel(), pointsDoubleBenefit);

        // 获取折扣值（从权益表获取，如果权益表没有则使用等级配置的默认值）
        Integer discount = 100; // 默认无折扣
        if (bookingDiscountBenefit != null && bookingDiscountBenefit.getBenefitValue() != null) {
            try {
                discount = Integer.parseInt(bookingDiscountBenefit.getBenefitValue());
            } catch (NumberFormatException e) {
                log.warn("预约折扣权益值格式错误，使用默认值100", e);
                discount = 100;
            }
        }

        // 获取积分倍数
        Integer pointsDouble = 1; // 默认1倍
        if (pointsDoubleBenefit != null && pointsDoubleBenefit.getBenefitValue() != null) {
            try {
                pointsDouble = Integer.parseInt(pointsDoubleBenefit.getBenefitValue());
            } catch (NumberFormatException e) {
                log.warn("积分倍数权益值格式错误，使用默认值1", e);
                pointsDouble = 1;
            }
        }

        // 计算折扣和优惠金额
        Long discountAmount = calculateDiscountAmount(order.getAmount(), discount);
        Long actualAmount = order.getAmount() - discountAmount;

        // 计算可获得积分（实际支付金额 * 积分倍数）
        Long getPoint = (long) (actualAmount * pointsDouble);

        // 构建支付信息VO
        SpH5PaymentVo vo = new SpH5PaymentVo();
        vo.setOrderId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setReservationId(order.getReservationId());
        vo.setAmount(order.getAmount());
        vo.setDiscount(discount);
        vo.setDiscountAmount(discountAmount);
        vo.setActualAmount(actualAmount);
        vo.setGetPoint(getPoint);
        vo.setBalance(memberCard.getBalance());
        vo.setTotalPoint(memberCard.getTotalPoint() != null ? memberCard.getTotalPoint() : 0L);
        vo.setCardLevel(memberCard.getCardLevel());
        vo.setCardLevelName(memberLevel.getLevelName());
        vo.setFreeTimes(memberCard.getFreeTimes() != null ? memberCard.getFreeTimes().intValue() : 0);
        vo.setPointsDouble(pointsDouble);
        vo.setBookingDiscount(discount);

        log.info("支付信息VO - freeTimes: {}, pointsDouble: {}, bookingDiscount: {}",
            vo.getFreeTimes(), vo.getPointsDouble(), vo.getBookingDiscount());

        return R.ok(vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> balancePay(Long orderId, Boolean useFreeTimes) {
        Long userId = LoginHelper.getUserId();

        // 查询订单信息
        SpOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            return R.fail("订单不存在");
        }

        // 验证订单是否属于当前用户
        if (!order.getUserId().equals(userId)) {
            return R.fail("无权支付该订单");
        }

        // 检查订单状态
        if (!ORDER_STATUS_PENDING.equals(order.getStatus()) ||
            !PAY_STATUS_PENDING.equals(order.getPayStatus())) {
            return R.fail("订单状态异常，无法支付");
        }

        // 使用分布式锁防止并发支付
        String lockKey = PAYMENT_LOCK_KEY + orderId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 尝试获取锁
            boolean acquired = lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS);
            if (!acquired) {
                return R.fail("支付处理中，请稍后重试");
            }

            try {
                // 再次查询订单状态（锁内二次验证）
                order = orderMapper.selectById(orderId);
                if (!ORDER_STATUS_PENDING.equals(order.getStatus()) ||
                    !PAY_STATUS_PENDING.equals(order.getPayStatus())) {
                    return R.fail("订单已支付或状态已变更");
                }

                // 查询会员卡信息
                SpMemberCard memberCard = memberCardMapper.selectOne(
                    new LambdaQueryWrapper<SpMemberCard>()
                        .eq(SpMemberCard::getUserId, userId)
                        .eq(SpMemberCard::getStatus, "0")
                );

                if (memberCard == null) {
                    return R.fail("会员卡不存在");
                }

                // 查询会员等级配置
                SpMemberLevel memberLevel = memberLevelMapper.selectOne(
                    new LambdaQueryWrapper<SpMemberLevel>()
                        .eq(SpMemberLevel::getLevelValue, Integer.parseInt(memberCard.getCardLevel()))
                );

                if (memberLevel == null) {
                    return R.fail("会员等级配置不存在");
                }

                // 查询预约折扣权益
                SpMemberBenefit bookingDiscountBenefit = memberBenefitMapper.selectOne(
                    new LambdaQueryWrapper<SpMemberBenefit>()
                        .eq(SpMemberBenefit::getStatus, "0")
                        .eq(SpMemberBenefit::getBenefitCode, "BOOKING_DISCOUNT")
                        .eq(SpMemberBenefit::getLevelValue, Integer.parseInt(memberCard.getCardLevel()))
                );

                // 查询积分倍数权益
                SpMemberBenefit pointsDoubleBenefit = memberBenefitMapper.selectOne(
                    new LambdaQueryWrapper<SpMemberBenefit>()
                        .eq(SpMemberBenefit::getStatus, "0")
                        .eq(SpMemberBenefit::getBenefitCode, "POINTS_DOUBLE")
                        .eq(SpMemberBenefit::getLevelValue, Integer.parseInt(memberCard.getCardLevel()))
                );

                // 获取折扣值（从权益表获取，如果权益表没有则使用等级配置的默认值）
                Integer discount = 100; // 默认无折扣
                if (bookingDiscountBenefit != null && bookingDiscountBenefit.getBenefitValue() != null) {
                    try {
                        discount = Integer.parseInt(bookingDiscountBenefit.getBenefitValue());
                    } catch (NumberFormatException e) {
                        log.warn("预约折扣权益值格式错误，使用默认值100", e);
                        discount = 100;
                    }
                }

                // 获取积分倍数
                Integer pointsDouble = 1; // 默认1倍
                if (pointsDoubleBenefit != null && pointsDoubleBenefit.getBenefitValue() != null) {
                    try {
                        pointsDouble = Integer.parseInt(pointsDoubleBenefit.getBenefitValue());
                    } catch (NumberFormatException e) {
                        log.warn("积分倍数权益值格式错误，使用默认值1", e);
                        pointsDouble = 1;
                    }
                }

                // 计算折扣和优惠金额
                Long discountAmount = calculateDiscountAmount(order.getAmount(), discount);
                Long actualAmount = order.getAmount() - discountAmount;

                // 判断是否使用免费次数支付
                if (Boolean.TRUE.equals(useFreeTimes)) {
                    // 使用免费次数支付
                    Long freeTimes = memberCard.getFreeTimes() != null ? memberCard.getFreeTimes() : 0L;
                    if (freeTimes < 1) {
                        return R.fail("免费次数不足");
                    }

                    // 扣除免费次数
                    memberCard.setFreeTimes(freeTimes - 1);

                    // 使用免费次数支付，获得0积分
                    Long getPoint = 0L;

                    // 更新订单信息
                    order.setDiscountAmount(discountAmount);
                    order.setActualAmount(0L); // 使用免费次数，实际支付为0
                    order.setGetPoint(getPoint);
                    order.setPayType("3"); // 免费次数支付
                    order.setPayStatus(PAY_STATUS_SUCCESS);
                    order.setStatus(ORDER_STATUS_PAID);
                    order.setPayTime(new Date());
                    orderMapper.updateById(order);

                    // 更新预约状态
                    SpReservation reservation = reservationMapper.selectById(order.getReservationId());
                    if (reservation != null) {
                        reservation.setStatus(RESERVATION_STATUS_PAID);
                        reservationMapper.updateById(reservation);
                    }

                    // 更新会员卡
                    memberCard.setTotalConsume(memberCard.getTotalConsume() + actualAmount);
                    memberCardMapper.updateById(memberCard);

                    return R.ok("支付成功");
                } else {
                    // 使用余额支付
                    // 检查余额是否足够
                    if (memberCard.getBalance() < actualAmount) {
                        return R.fail("余额不足，请先充值");
                    }

                    // 计算获得积分（实际支付金额 * 积分倍数）
                    Long getPoint = (long) (actualAmount * pointsDouble);

                    // 更新订单信息
                    order.setDiscountAmount(discountAmount);
                    order.setActualAmount(actualAmount);
                    order.setGetPoint(getPoint);
                    order.setPayType("2"); // 余额支付
                    order.setPayStatus(PAY_STATUS_SUCCESS);
                    order.setStatus(ORDER_STATUS_PAID);
                    order.setPayTime(new Date());
                    orderMapper.updateById(order);

                    // 更新预约状态
                    SpReservation reservation = reservationMapper.selectById(order.getReservationId());
                    if (reservation != null) {
                        reservation.setStatus(RESERVATION_STATUS_PAID);
                        reservationMapper.updateById(reservation);
                    }

                    // 更新会员卡余额和积分
                    memberCard.setBalance(memberCard.getBalance() - actualAmount);
                    memberCard.setTotalConsume(memberCard.getTotalConsume() + actualAmount);
                    memberCard.setTotalPoint(
                        (memberCard.getTotalPoint() != null ? memberCard.getTotalPoint() : 0L) + getPoint
                    );
                    memberCardMapper.updateById(memberCard);

                    return R.ok("支付成功");
                }
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取支付锁失败", e);
            return R.fail("支付失败，请稍后重试");
        } catch (Exception e) {
            log.error("余额支付失败", e);
            throw new ServiceException("支付失败");
        }
    }

    /**
     * 计算优惠金额
     * @param amount 原始金额
     * @param discount 折扣（例如95表示95折）
     * @return 优惠金额
     */
    private Long calculateDiscountAmount(Long amount, Integer discount) {
        if (discount == null || discount >= 100) {
            return 0L;
        }
        BigDecimal amountDecimal = new BigDecimal(amount);
        BigDecimal discountDecimal = new BigDecimal(discount);
        BigDecimal discountAmount = amountDecimal.multiply(
            new BigDecimal(100).subtract(discountDecimal)
        ).divide(new BigDecimal(100), 0, RoundingMode.DOWN);
        return discountAmount.longValue();
    }

    /**
     * 根据权益编码获取权益类型
     */
    private String getBenefitType(String benefitCode) {
        if (benefitCode.contains("discount")) {
            return "1"; // 折扣
        } else if (benefitCode.contains("free")) {
            return "2"; // 免费次数
        } else if (benefitCode.contains("delay")) {
            return "3"; // 延迟时长
        } else if (benefitCode.contains("point")) {
            return "4"; // 积分倍率
        }
        return "0"; // 其他
    }
}
