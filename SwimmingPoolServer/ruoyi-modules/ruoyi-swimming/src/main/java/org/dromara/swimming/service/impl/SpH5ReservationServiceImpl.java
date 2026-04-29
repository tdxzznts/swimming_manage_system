package org.dromara.swimming.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.swimming.domain.SpOrder;
import org.dromara.swimming.domain.SpMemberCard;
import org.dromara.swimming.mapper.SpMemberCardMapper;
import org.dromara.swimming.mapper.SpOrderMapper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.dromara.swimming.domain.SpLane;
import org.dromara.swimming.domain.SpReservation;
import org.dromara.swimming.domain.SpTimeSlot;
import org.dromara.swimming.domain.bo.SpH5MyReservationBo;
import org.dromara.swimming.domain.bo.SpH5ReservationBo;
import org.dromara.swimming.domain.vo.SpH5LaneVo;
import org.dromara.swimming.domain.vo.SpH5ReservationVo;
import org.dromara.swimming.domain.vo.SpH5TimeSlotVo;
import org.dromara.swimming.mapper.SpLaneMapper;
import org.dromara.swimming.mapper.SpReservationMapper;
import org.dromara.swimming.mapper.SpTimeSlotMapper;
import org.dromara.swimming.service.ISpH5ReservationService;

/**
 * H5预约服务实现
 *
 * @author swimming
 * @date 2026-02-03
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SpH5ReservationServiceImpl implements ISpH5ReservationService {

    private final SpLaneMapper laneMapper;
    private final SpTimeSlotMapper timeSlotMapper;
    private final SpReservationMapper reservationMapper;
    private final RedissonClient redissonClient;
    private final SpOrderMapper orderMapper;
    private final SpMemberCardMapper memberCardMapper;

    // 泳道类型字典
    private static final String LANE_TYPE_FAST = "1";
    private static final String LANE_TYPE_MEDIUM = "2";
    private static final String LANE_TYPE_SLOW = "3";

    // 泳道状态字典
    private static final String LANE_STATUS_OPEN = "0";
    private static final String LANE_STATUS_CLOSED = "1";
    private static final String LANE_STATUS_MAINTENANCE = "2";

    // 时段类型字典
    private static final String SLOT_TYPE_PEAK = "1";
    private static final String SLOT_TYPE_OFF_PEAK = "2";

    // 预约状态字典
    private static final String RESERVATION_STATUS_PENDING = "0";
    private static final String RESERVATION_STATUS_PAID = "1";
    private static final String RESERVATION_STATUS_COMPLETED = "2";
    private static final String RESERVATION_STATUS_CANCELLED = "3";
    private static final String RESERVATION_STATUS_REFUNDED = "4";

    // 订单状态字典
    private static final String ORDER_STATUS_PENDING = "0";
    private static final String ORDER_STATUS_PAID = "1";
    private static final String ORDER_STATUS_COMPLETED = "2";
    private static final String ORDER_STATUS_CANCELLED = "3";
    private static final String ORDER_STATUS_REFUNDED = "4";

    // 分布式锁key前缀
    private static final String RESERVATION_LOCK_KEY = "reservation:lock:";
    private static final long LOCK_WAIT_TIME = 3L; // 锁等待时间（秒）
    private static final long LOCK_LEASE_TIME = 10L; // 锁持有时间（秒）

    @Override
    public R<List<SpH5LaneVo>> listLanes() {
        // 查询开放状态的泳道
        List<SpLane> lanes = laneMapper.selectList(
            new LambdaQueryWrapper<SpLane>()
                .eq(SpLane::getStatus, LANE_STATUS_OPEN)
                .orderByAsc(SpLane::getSort)
        );

        List<SpH5LaneVo> result = new ArrayList<>();
        for (SpLane lane : lanes) {
            SpH5LaneVo vo = new SpH5LaneVo();
            vo.setId(lane.getId());
            vo.setLaneNo(lane.getLaneNo());
            vo.setLaneType(lane.getLaneType());
            vo.setLaneTypeName(getLaneTypeName(lane.getLaneType()));
            vo.setCapacity(lane.getCapacity().intValue());
            vo.setLength(lane.getLength().intValue());
            vo.setWidth(lane.getWidth().intValue());
            vo.setDepth(lane.getDepth().intValue());
            vo.setStatus(lane.getStatus());
            vo.setStatusName(getLaneStatusName(lane.getStatus()));
            vo.setSort(lane.getSort().intValue());
            result.add(vo);
        }

        return R.ok(result);
    }

    @Override
    public R<List<SpH5TimeSlotVo>> listTimeSlots(Date date, Long laneId) {
        // 打印调试日志
        log.info("查询时段 - 参数: date={}, laneId={}", date, laneId);

        // 清除时分秒，只保留日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date dayStart = calendar.getTime();

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date dayEnd = calendar.getTime();

        log.info("查询时段 - 日期范围: dayStart={}, dayEnd={}", dayStart, dayEnd);

        // 先查询所有时段看看数据
        LambdaQueryWrapper<SpTimeSlot> allWrapper = new LambdaQueryWrapper<SpTimeSlot>()
            .eq(SpTimeSlot::getIsAvailable, "1");
        List<SpTimeSlot> allSlots = timeSlotMapper.selectList(allWrapper);
        log.info("查询时段 - 所有可用时段数量: {}", allSlots.size());
        for (SpTimeSlot slot : allSlots) {
            log.info("时段数据: id={}, laneId={}, slotDate={}, startTime={}, endTime={}",
                slot.getId(), slot.getLaneId(), slot.getSlotDate(), slot.getStartTime(), slot.getEndTime());
        }

        // 查询指定日期的可用时段（使用日期范围查询）
        LambdaQueryWrapper<SpTimeSlot> queryWrapper = new LambdaQueryWrapper<SpTimeSlot>()
            .ge(SpTimeSlot::getSlotDate, dayStart)
            .lt(SpTimeSlot::getSlotDate, dayEnd)
            .eq(SpTimeSlot::getIsAvailable, "1")
            .orderByAsc(SpTimeSlot::getStartTime);

        // 如果指定了泳道ID，添加过滤条件
        if (laneId != null) {
            queryWrapper.eq(SpTimeSlot::getLaneId, laneId);
            log.info("查询时段 - 添加泳道过滤: laneId={}", laneId);
        }

        List<SpTimeSlot> timeSlots = timeSlotMapper.selectList(queryWrapper);
        log.info("查询时段 - 查询结果数量: {}", timeSlots.size());

        // 获取当前时间
        Date now = new Date();
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String todayStr = dayFormat.format(now);
        String dateStr = dayFormat.format(date);
        boolean isToday = todayStr.equals(dateStr);

        // 获取当前时间字符串（HH:mm:ss）
        String currentTimeStr = timeFormat.format(now);

        List<SpH5TimeSlotVo> result = new ArrayList<>();
        for (SpTimeSlot slot : timeSlots) {
            // 如果是今天，过滤掉已经过去的时段
            if (isToday) {
                // 只比较时间部分（HH:mm:ss）
                String endTimeStr = timeFormat.format(slot.getEndTime());
                if (endTimeStr.compareTo(currentTimeStr) < 0) {
                    log.info("过滤已结束时段: endTimeStr={}, currentTimeStr={}", endTimeStr, currentTimeStr);
                    continue; // 跳过已经结束的时段
                }
            }

            // 获取泳道信息
            SpLane lane = laneMapper.selectById(slot.getLaneId());
            if (lane == null) {
                continue;
            }

            SpH5TimeSlotVo vo = new SpH5TimeSlotVo();
            vo.setId(slot.getId());
            vo.setLaneId(slot.getLaneId());
            vo.setLaneNo(lane.getLaneNo());
            vo.setLaneType(lane.getLaneType());
            vo.setSlotDate(slot.getSlotDate());
            vo.setStartTime(slot.getStartTime());
            vo.setEndTime(slot.getEndTime());
            vo.setSlotType(slot.getSlotType());
            vo.setSlotTypeName(getSlotTypeName(slot.getSlotType()));
            vo.setCapacity(slot.getCapacity().intValue());
            vo.setBookedCount(slot.getBookedCount().intValue());
            vo.setRemainingCount(slot.getCapacity().intValue() - slot.getBookedCount().intValue());
            vo.setPrice(slot.getPrice());
            vo.setIsAvailable(slot.getIsAvailable());

            result.add(vo);
        }

        return R.ok(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<SpH5ReservationVo> createReservation(SpH5ReservationBo bo) {
        Long userId = LoginHelper.getUserId();
        Long timeSlotId = bo.getTimeSlotId();
        Date reservationDate = bo.getReservationDate();

        // 获取时段信息
        SpTimeSlot timeSlot = timeSlotMapper.selectById(timeSlotId);
        if (timeSlot == null) {
            return R.fail("时段不存在");
        }

        // 检查时段是否可预约
        if (!"1".equals(timeSlot.getIsAvailable())) {
            return R.fail("该时段暂不可预约");
        }

        // 检查容量是否已满
        if (timeSlot.getBookedCount() >= timeSlot.getCapacity()) {
            return R.fail("该时段预约人数已满");
        }

        // 使用分布式锁防止并发冲突
        String lockKey = RESERVATION_LOCK_KEY + timeSlotId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 尝试获取锁
            boolean acquired = lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS);
            if (!acquired) {
                return R.fail("当前预约人数较多，请稍后重试");
            }

            try {
                // 再次检查时段状态（锁内二次验证）
                timeSlot = timeSlotMapper.selectById(timeSlotId);
                if (timeSlot.getBookedCount() >= timeSlot.getCapacity()) {
                    return R.fail("该时段预约人数已满");
                }

                // 检查用户是否已预约该时段
                long existsCount = reservationMapper.selectCount(
                    new LambdaQueryWrapper<SpReservation>()
                        .eq(SpReservation::getUserId, userId)
                        .eq(SpReservation::getTimeSlotId, timeSlotId)
                        .in(SpReservation::getStatus,
                            List.of(RESERVATION_STATUS_PENDING, RESERVATION_STATUS_PAID, RESERVATION_STATUS_COMPLETED))
                );
                if (existsCount > 0) {
                    return R.fail("您已预约该时段，请勿重复预约");
                }
                // 先创建预约记录（获取预约ID）
                SpReservation reservation = new SpReservation();
                reservation.setUserId(userId);
                reservation.setTimeSlotId(timeSlotId);
                reservation.setLaneId(timeSlot.getLaneId());
                reservation.setReservationNo(generateReservationNo());
                reservation.setReservationDate(reservationDate);
                reservation.setStartTime(timeSlot.getStartTime());
                reservation.setEndTime(timeSlot.getEndTime());
                reservation.setStatus(RESERVATION_STATUS_PENDING);
                reservation.setAmount(timeSlot.getPrice());

                reservationMapper.insert(reservation);

                // 生成订单号
                String orderNo = generateOrderNo();
                // 创建订单（关联预约ID）
                SpOrder order = new SpOrder();
                order.setOrderNo(orderNo);
                order.setUserId(userId);
                order.setReservationId(reservation.getId());
                order.setOrderType("1"); // 1-预约
                order.setAmount(timeSlot.getPrice());
                order.setDiscountAmount(0l);
                order.setActualAmount(0l);
                order.setPayStatus("0"); // 0-待支付
                order.setStatus("0"); // 0-待支付
                order.setCreateTime(new Date());
                orderMapper.insert(order);

                // 更新预约记录的订单ID
                reservation.setOrderId(order.getId());
                reservationMapper.updateById(reservation);

                // 更新时段已预约人数
                timeSlot.setBookedCount(timeSlot.getBookedCount() + 1);
                timeSlotMapper.updateById(timeSlot);

                // 构建返回对象
                SpH5ReservationVo vo = buildReservationVo(reservation, timeSlot);

                return R.ok(vo);
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取预约锁失败", e);
            return R.fail("预约失败，请稍后重试");
        } catch (Exception e) {
            log.error("创建预约失败", e);
            throw new ServiceException("预约失败");
        }
    }
    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "RES" + System.currentTimeMillis() +
            (UUID.randomUUID().toString().replace("-", "").substring(0, 6)).toUpperCase();
    }
    @Override
    public R<List<SpH5ReservationVo>> listMyReservations(SpH5MyReservationBo bo) {
        Long userId = LoginHelper.getUserId();

        LambdaQueryWrapper<SpReservation> wrapper = new LambdaQueryWrapper<SpReservation>()
            .eq(SpReservation::getUserId, userId)
            .orderByDesc(SpReservation::getReservationDate)
            .orderByDesc(SpReservation::getCreateTime);

        // 状态筛选
        if (StringUtils.isNotBlank(bo.getStatus())) {
            wrapper.eq(SpReservation::getStatus, bo.getStatus());
        }

        // 日期范围筛选
        if (bo.getStartDate() != null) {
            wrapper.ge(SpReservation::getReservationDate, bo.getStartDate());
        }
        if (bo.getEndDate() != null) {
            wrapper.le(SpReservation::getReservationDate, bo.getEndDate());
        }

        List<SpReservation> reservations = reservationMapper.selectList(wrapper);

        List<SpH5ReservationVo> result = new ArrayList<>();
        for (SpReservation reservation : reservations) {
            SpTimeSlot timeSlot = timeSlotMapper.selectById(reservation.getTimeSlotId());
            result.add(buildReservationVo(reservation, timeSlot));
        }

        return R.ok(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> cancelReservation(Long reservationId) {
        Long userId = LoginHelper.getUserId();

        // 查询预约记录
        SpReservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            return R.fail("预约记录不存在");
        }

        // 验证是否是本人预约
        if (!reservation.getUserId().equals(userId)) {
            return R.fail("无权取消该预约");
        }

        // 检查预约状态
        if (!RESERVATION_STATUS_PENDING.equals(reservation.getStatus()) &&
            !RESERVATION_STATUS_PAID.equals(reservation.getStatus())) {
            return R.fail("该预约不可取消");
        }

        // 查询时段信息，检查是否在时段开始前
        SpTimeSlot timeSlot = timeSlotMapper.selectById(reservation.getTimeSlotId());
        if (timeSlot != null) {
            // 构建时段开始时间
            Date reservationDate = reservation.getReservationDate();
            Date startTime = timeSlot.getStartTime();

            // 合并日期和时间
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.setTime(reservationDate);
            int year = calendar.get(java.util.Calendar.YEAR);
            int month = calendar.get(java.util.Calendar.MONTH);
            int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);

            calendar.setTime(startTime);
            calendar.set(year, month, day);

            Date slotStartDateTime = calendar.getTime();

            // 检查当前时间是否已过时段开始时间
            Date now = new Date();
            if (!now.before(slotStartDateTime)) {
                return R.fail("预约时段已开始或已结束，无法取消");
            }
        }

        // 查询订单信息
        SpOrder order = orderMapper.selectOne(
            new LambdaQueryWrapper<SpOrder>()
                .eq(SpOrder::getReservationId, reservation.getId())
        );

        // 如果有订单，处理退款
        if (order != null && RESERVATION_STATUS_PAID.equals(reservation.getStatus())) {
            // 检查订单状态
            if (!ORDER_STATUS_PENDING.equals(order.getStatus()) &&
                !ORDER_STATUS_PAID.equals(order.getStatus())) {
                return R.fail("订单状态异常，无法取消");
            }

            // 根据支付方式处理退款
            String payType = order.getPayType();
            if ("3".equals(payType)) {
                // 免费次数支付：恢复免费次数
                handleFreeTimesRefund(userId);
            } else if ("2".equals(payType)) {
                // 余额支付：返还余额和积分
                handleBalanceRefund(userId, order.getActualAmount(), order.getGetPoint());
            }
            // 微信支付暂不处理退款
        }

        // 更新预约状态
        reservation.setStatus(RESERVATION_STATUS_CANCELLED);
        reservation.setCancelTime(new Date());
        reservationMapper.updateById(reservation);

        // 更新订单状态
        if (order != null) {
            order.setStatus(ORDER_STATUS_CANCELLED);
            orderMapper.updateById(order);
        }

        // 释放时段名额
        if (timeSlot != null && timeSlot.getBookedCount() > 0) {
            timeSlot.setBookedCount(timeSlot.getBookedCount() - 1);
            timeSlotMapper.updateById(timeSlot);
        }

        return R.ok("取消成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> deleteReservation(Long reservationId) {
        Long userId = LoginHelper.getUserId();

        // 查询预约记录
        SpReservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            return R.fail("预约记录不存在");
        }

        // 验证是否是本人预约
        if (!reservation.getUserId().equals(userId)) {
            return R.fail("无权删除该预约");
        }

        // 检查预约状态，只允许删除已取消的预约
        if (!RESERVATION_STATUS_CANCELLED.equals(reservation.getStatus())) {
            return R.fail("只能删除已取消的预约记录");
        }

        // 删除预约记录（逻辑删除）
        reservationMapper.deleteById(reservationId);

        log.info("用户删除已取消的预约：预约ID={}，用户ID={}", reservationId, userId);

        return R.ok("删除成功");
    }

    /**
     * 处理免费次数退款
     */
    private void handleFreeTimesRefund(Long userId) {
        SpMemberCard memberCard = memberCardMapper.selectOne(
            new LambdaQueryWrapper<SpMemberCard>()
                .eq(SpMemberCard::getUserId, userId)
                .eq(SpMemberCard::getStatus, "0")
        );

        if (memberCard != null) {
            Long currentFreeTimes = memberCard.getFreeTimes() != null ? memberCard.getFreeTimes() : 0L;
            memberCard.setFreeTimes(currentFreeTimes + 1);
            memberCardMapper.updateById(memberCard);
            log.info("用户{}取消订单，恢复免费次数1次", userId);
        }
    }

    /**
     * 处理余额和积分退款
     */
    private void handleBalanceRefund(Long userId, Long refundAmount, Long refundPoints) {
        SpMemberCard memberCard = memberCardMapper.selectOne(
            new LambdaQueryWrapper<SpMemberCard>()
                .eq(SpMemberCard::getUserId, userId)
                .eq(SpMemberCard::getStatus, "0")
        );

        if (memberCard != null) {
            // 返还余额
            if (refundAmount != null && refundAmount > 0) {
                memberCard.setBalance(memberCard.getBalance() + refundAmount);
                // 减少累计消费
                memberCard.setTotalConsume(
                    Math.max(0, memberCard.getTotalConsume() - refundAmount)
                );
            }

            // 返还积分
            if (refundPoints != null && refundPoints > 0) {
                Long currentPoints = memberCard.getTotalPoint() != null ? memberCard.getTotalPoint() : 0L;
                memberCard.setTotalPoint(Math.max(0, currentPoints - refundPoints));
            }

            memberCardMapper.updateById(memberCard);
            log.info("用户{}取消订单，返还余额{}分，扣除积分{}分", userId, refundAmount, refundPoints);
        }
    }

    /**
     * 构建预约返回对象
     */
    private SpH5ReservationVo buildReservationVo(SpReservation reservation, SpTimeSlot timeSlot) {
        SpH5ReservationVo vo = new SpH5ReservationVo();
        vo.setId(reservation.getId());
        vo.setReservationNo(reservation.getReservationNo());
        vo.setReservationDate(reservation.getReservationDate());
        vo.setStartTime(reservation.getStartTime());
        vo.setEndTime(reservation.getEndTime());
        vo.setStatus(reservation.getStatus());
        vo.setStatusName(getReservationStatusName(reservation.getStatus()));
        vo.setAmount(reservation.getAmount());
        vo.setCheckInTime(reservation.getCheckInTime());
        vo.setCheckOutTime(reservation.getCheckOutTime());
        vo.setCancelTime(reservation.getCancelTime());

        // 判断是否可取消
        vo.setCanCancel(canCancelReservation(reservation.getStatus()));

        // 获取订单ID和金额信息
        SpOrder order = orderMapper.selectOne(
            new LambdaQueryWrapper<SpOrder>()
                .eq(SpOrder::getReservationId, reservation.getId())
        );
        if (order != null) {
            vo.setOrderId(order.getId());
            vo.setDiscountAmount(order.getDiscountAmount());
            vo.setActualAmount(order.getActualAmount());
            vo.setPayType(order.getPayType());
            vo.setPayTypeName(getPayTypeName(order.getPayType()));
        }

        // 获取泳道信息
        if (timeSlot != null) {
            SpLane lane = laneMapper.selectById(timeSlot.getLaneId());
            if (lane != null) {
                vo.setLaneNo(lane.getLaneNo());
                vo.setLaneTypeName(getLaneTypeName(lane.getLaneType()));
            }
        }

        return vo;
    }

    /**
     * 判断预约是否可取消
     */
    private boolean canCancelReservation(String status) {
        return RESERVATION_STATUS_PENDING.equals(status) ||
               RESERVATION_STATUS_PAID.equals(status);
    }

    /**
     * 生成预约单号
     */
    private String generateReservationNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return "R" + sdf.format(new Date()) + (int) (Math.random() * 1000);
    }

    /**
     * 获取泳道类型名称
     */
    private String getLaneTypeName(String laneType) {
        return switch (laneType) {
            case LANE_TYPE_FAST -> "快速";
            case LANE_TYPE_MEDIUM -> "中速";
            case LANE_TYPE_SLOW -> "慢速";
            default -> "未知";
        };
    }

    /**
     * 获取泳道状态名称
     */
    private String getLaneStatusName(String status) {
        return switch (status) {
            case LANE_STATUS_OPEN -> "开放";
            case LANE_STATUS_CLOSED -> "关闭";
            case LANE_STATUS_MAINTENANCE -> "维修";
            default -> "未知";
        };
    }

    /**
     * 获取时段类型名称
     */
    private String getSlotTypeName(String slotType) {
        return SLOT_TYPE_PEAK.equals(slotType) ? "高峰期" : "低峰期";
    }

    /**
     * 获取预约状态名称
     */
    private String getReservationStatusName(String status) {
        return switch (status) {
            case RESERVATION_STATUS_PENDING -> "待支付";
            case RESERVATION_STATUS_PAID -> "已支付";
            case RESERVATION_STATUS_COMPLETED -> "已完成";
            case RESERVATION_STATUS_CANCELLED -> "已取消";
            case RESERVATION_STATUS_REFUNDED -> "已退款";
            default -> "未知";
        };
    }

    /**
     * 获取支付方式名称
     */
    private String getPayTypeName(String payType) {
        if (payType == null) {
            return null;
        }
        return switch (payType) {
            case "1" -> "微信支付";
            case "2" -> "余额支付";
            case "3" -> "免费次数";
            default -> "未知";
        };
    }

    @Override
    public R<org.dromara.swimming.domain.vo.SpH5HomeStatisticsVo> getHomeStatistics() {
        org.dromara.swimming.domain.vo.SpH5HomeStatisticsVo vo = new org.dromara.swimming.domain.vo.SpH5HomeStatisticsVo();

        try {
            // 1. 查询今日剩余时间段（未结束且还有剩余名额的时段数量）
            Date now = new Date();

            // 获取今天的开始和结束时间
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date todayStart = calendar.getTime();

            calendar.add(Calendar.DAY_OF_MONTH, 1);
            Date tomorrowStart = calendar.getTime();

            // 查询今天所有时段
            LambdaQueryWrapper<org.dromara.swimming.domain.SpTimeSlot> slotWrapper = new LambdaQueryWrapper<>();
            slotWrapper.ge(org.dromara.swimming.domain.SpTimeSlot::getSlotDate, todayStart);
            slotWrapper.lt(org.dromara.swimming.domain.SpTimeSlot::getSlotDate, tomorrowStart);
            slotWrapper.eq(org.dromara.swimming.domain.SpTimeSlot::getDelFlag, "0");
            slotWrapper.eq(org.dromara.swimming.domain.SpTimeSlot::getIsAvailable, "1");
            slotWrapper.orderByAsc(org.dromara.swimming.domain.SpTimeSlot::getStartTime);

            List<org.dromara.swimming.domain.SpTimeSlot> todaySlots = timeSlotMapper.selectList(slotWrapper);

            int remainingSlots = 0;
            for (org.dromara.swimming.domain.SpTimeSlot slot : todaySlots) {
                // 检查时段是否已结束
                // 合并日期和时间
                Calendar slotEndCalendar = Calendar.getInstance();
                slotEndCalendar.setTime(slot.getSlotDate());
                int year = slotEndCalendar.get(Calendar.YEAR);
                int month = slotEndCalendar.get(Calendar.MONTH);
                int day = slotEndCalendar.get(Calendar.DAY_OF_MONTH);

                slotEndCalendar.setTime(slot.getEndTime());
                slotEndCalendar.set(year, month, day);

                Date slotEndDateTime = slotEndCalendar.getTime();

                // 计算剩余数量
                long remainingCount = slot.getCapacity() - slot.getBookedCount();

                if (now.before(slotEndDateTime) && remainingCount > 0) {
                    remainingSlots++;
                }
            }
            vo.setRemainingSlots(remainingSlots);

            // 2. 查询当前在场人数（已签到但未签退的预约数量）
            LambdaQueryWrapper<org.dromara.swimming.domain.SpReservation> reservationWrapper = new LambdaQueryWrapper<>();
            reservationWrapper.eq(org.dromara.swimming.domain.SpReservation::getStatus, RESERVATION_STATUS_PAID);
            reservationWrapper.isNotNull(org.dromara.swimming.domain.SpReservation::getCheckInTime);
            reservationWrapper.isNull(org.dromara.swimming.domain.SpReservation::getCheckOutTime);

            List<org.dromara.swimming.domain.SpReservation> activeReservations = reservationMapper.selectList(reservationWrapper);
            vo.setCurrentPeopleCount(activeReservations.size());

            return R.ok(vo);
        } catch (Exception e) {
            log.error("获取首页统计信息失败", e);
            // 发生错误时返回默认值
            vo.setRemainingSlots(0);
            vo.setCurrentPeopleCount(0);
            return R.ok(vo);
        }
    }
}
