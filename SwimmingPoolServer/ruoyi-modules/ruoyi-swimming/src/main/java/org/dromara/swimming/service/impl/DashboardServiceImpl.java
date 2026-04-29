package org.dromara.swimming.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.swimming.domain.SpIotDeviceAlarm;
import org.dromara.swimming.domain.SpIotDeviceData;
import org.dromara.swimming.domain.SpMemberCard;
import org.dromara.swimming.domain.SpOrder;
import org.dromara.swimming.domain.SpReservation;
import org.dromara.swimming.domain.vo.DashboardStatisticsVo;
import org.dromara.swimming.domain.vo.TrendDataVo;
import org.dromara.swimming.mapper.SpIotDeviceAlarmMapper;
import org.dromara.swimming.mapper.SpIotDeviceDataMapper;
import org.dromara.swimming.mapper.SpIotDeviceMapper;
import org.dromara.swimming.mapper.SpMemberCardMapper;
import org.dromara.swimming.mapper.SpOrderMapper;
import org.dromara.swimming.mapper.SpReservationMapper;
import org.dromara.swimming.service.IDashboardService;
import org.dromara.swimming.service.ISpIotDeviceService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 首页统计服务实现
 *
 * @author swimming
 * @date 2026-02-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements IDashboardService {

    private final ISpIotDeviceService deviceService;
    private final SpIotDeviceMapper deviceMapper;
    private final SpIotDeviceDataMapper deviceDataMapper;
    private final SpIotDeviceAlarmMapper deviceAlarmMapper;
    private final SpMemberCardMapper memberCardMapper;
    private final SpReservationMapper reservationMapper;
    private final SpOrderMapper orderMapper;

    @Override
    public DashboardStatisticsVo getDashboardStatistics() {
        DashboardStatisticsVo vo = new DashboardStatisticsVo();
        vo.setUpdateTime(LocalDateTime.now());

        // 会员统计
        vo.setMemberStats(getMemberStatistics());

        // 预约统计
        vo.setReservationStats(getReservationStatistics());

        // 订单统计
        vo.setOrderStats(getOrderStatistics());

        // 设备统计
        vo.setDeviceStats(getDeviceStatistics());

        // 水质实时数据
        vo.setWaterQuality(getWaterQualityData());

        return vo;
    }

    /**
     * 获取会员统计
     */
    private DashboardStatisticsVo.MemberStatistics getMemberStatistics() {
        DashboardStatisticsVo.MemberStatistics stats = new DashboardStatisticsVo.MemberStatistics();

        // 查询会员总数
        Long totalCount = memberCardMapper.selectCount(null);
        stats.setTotalMembers(totalCount.intValue());

        // 查询今日新增会员数
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        Long todayCount = memberCardMapper.selectCount(
            new LambdaQueryWrapper<SpMemberCard>()
                .ge(SpMemberCard::getCreateTime, todayStart)
                .le(SpMemberCard::getCreateTime, todayEnd)
        );
        stats.setTodayNewMembers(todayCount.intValue());

        // 查询会员总余额（单位：元）
        List<SpMemberCard> allCards = memberCardMapper.selectList(null);
        long totalBalance = allCards.stream()
            .mapToLong(card -> card.getBalance() != null ? card.getBalance() : 0L)
            .sum();
        stats.setTotalBalance(new BigDecimal(totalBalance));

        return stats;
    }

    /**
     * 获取预约统计
     */
    private DashboardStatisticsVo.ReservationStatistics getReservationStatistics() {
        DashboardStatisticsVo.ReservationStatistics stats = new DashboardStatisticsVo.ReservationStatistics();

        // 今日日期范围
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        Date todayStartDate = Date.from(todayStart.atZone(java.time.ZoneId.systemDefault()).toInstant());
        Date todayEndDate = Date.from(todayEnd.atZone(java.time.ZoneId.systemDefault()).toInstant());

        // 查询今日预约数（已支付和已完成）
        Long todayReservations = reservationMapper.selectCount(
            new LambdaQueryWrapper<SpReservation>()
                .ge(SpReservation::getReservationDate, todayStartDate)
                .le(SpReservation::getReservationDate, todayEndDate)
                .in(SpReservation::getStatus, "1", "2") // 1已支付 2已完成
        );
        stats.setTodayReservations(todayReservations.intValue());

        // 查询当前在场人数（通过预约的起止时间判断）
        Date now = new Date();
        Long currentPresent = reservationMapper.selectCount(
            new LambdaQueryWrapper<SpReservation>()
                .le(SpReservation::getStartTime, now)
                .ge(SpReservation::getEndTime, now)
                .in(SpReservation::getStatus, "1", "2") // 1已支付 2已完成
        );
        stats.setCurrentPresent(currentPresent.intValue());

        return stats;
    }

    /**
     * 获取订单统计
     */
    private DashboardStatisticsVo.OrderStatistics getOrderStatistics() {
        DashboardStatisticsVo.OrderStatistics stats = new DashboardStatisticsVo.OrderStatistics();

        // 今日日期范围
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        // 查询今日充值订单数（orderType=2 且已支付）
        Long todayOrders = orderMapper.selectCount(
            new LambdaQueryWrapper<SpOrder>()
                .eq(SpOrder::getOrderType, "2") // 2充值
                .eq(SpOrder::getPayStatus, "1") // 1已支付
                .ge(SpOrder::getCreateTime, todayStart)
                .le(SpOrder::getCreateTime, todayEnd)
        );
        stats.setTodayOrders(todayOrders.intValue());

        // 查询今日充值收入（amount，单位：元）
        List<SpOrder> todayOrderList = orderMapper.selectList(
            new LambdaQueryWrapper<SpOrder>()
                .eq(SpOrder::getOrderType, "2") // 2充值
                .eq(SpOrder::getPayStatus, "1") // 1已支付
                .ge(SpOrder::getCreateTime, todayStart)
                .le(SpOrder::getCreateTime, todayEnd)
        );
        long todayIncome = todayOrderList.stream()
            .mapToLong(order -> order.getAmount() != null ? order.getAmount() : 0L)
            .sum();
        stats.setTodayIncome(new BigDecimal(todayIncome));

        // 查询本月充值收入
        LocalDateTime monthStart = LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN);
        List<SpOrder> monthOrderList = orderMapper.selectList(
            new LambdaQueryWrapper<SpOrder>()
                .eq(SpOrder::getOrderType, "2") // 2充值
                .eq(SpOrder::getPayStatus, "1") // 1已支付
                .ge(SpOrder::getCreateTime, monthStart)
        );
        long monthIncome = monthOrderList.stream()
            .mapToLong(order -> order.getAmount() != null ? order.getAmount() : 0L)
            .sum();
        stats.setMonthIncome(new BigDecimal(monthIncome));

        return stats;
    }

    /**
     * 获取设备统计
     */
    private DashboardStatisticsVo.DeviceStatistics getDeviceStatistics() {
        DashboardStatisticsVo.DeviceStatistics stats = new DashboardStatisticsVo.DeviceStatistics();

        // 使用Service的统计方法
        Map<String, Object> deviceStats = deviceService.getDeviceStatistics();
        Object totalCount = deviceStats.get("totalCount");
        Object onlineCount = deviceStats.get("onlineCount");

        int totalDevices = 0;
        int onlineDevices = 0;

        if (totalCount instanceof Long) {
            totalDevices = ((Long) totalCount).intValue();
        } else if (totalCount instanceof Integer) {
            totalDevices = (Integer) totalCount;
        }

        if (onlineCount instanceof Long) {
            onlineDevices = ((Long) onlineCount).intValue();
        } else if (onlineCount instanceof Integer) {
            onlineDevices = (Integer) onlineCount;
        }

        stats.setTotalDevices(totalDevices);
        stats.setOnlineDevices(onlineDevices);
        stats.setOfflineDevices(totalDevices - onlineDevices);

        // 查询今日告警数
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        Long todayAlarms = deviceAlarmMapper.selectCount(
            new LambdaQueryWrapper<SpIotDeviceAlarm>()
                .ge(SpIotDeviceAlarm::getCreateTime, todayStart)
        );
        stats.setTodayAlarms(todayAlarms.intValue());

        return stats;
    }

    /**
     * 获取水质实时数据
     */
    private DashboardStatisticsVo.WaterQualityData getWaterQualityData() {
        DashboardStatisticsVo.WaterQualityData waterQuality = new DashboardStatisticsVo.WaterQualityData();

        // 获取最新的设备数据
        List<SpIotDeviceData> dataList = deviceDataMapper.selectList(
            new LambdaQueryWrapper<SpIotDeviceData>()
                .orderByDesc(SpIotDeviceData::getCollectTime)
                .last("LIMIT 1")
        );

        log.info("查询设备数据，结果数量: {}", dataList.size());

        if (!dataList.isEmpty()) {
            SpIotDeviceData latestData = dataList.get(0);
            try {
                // 解析params字段获取水质数据
                String params = latestData.getParams();
                log.info("最新设备数据 - DeviceKey: {}, Params: {}", latestData.getDeviceKey(), params);

                if (params != null && !params.isEmpty()) {
                    // 使用Hutool JSON解析
                    cn.hutool.json.JSONObject jsonObject = cn.hutool.json.JSONUtil.parseObj(params);

                    if (jsonObject.containsKey("temperature")) {
                        Object temp = jsonObject.get("temperature");
                        waterQuality.setTemperature(new BigDecimal(temp.toString()));
                    }
                    if (jsonObject.containsKey("ph")) {
                        Object ph = jsonObject.get("ph");
                        waterQuality.setPh(new BigDecimal(ph.toString()));
                    }
                    if (jsonObject.containsKey("chlorine")) {
                        Object chlorine = jsonObject.get("chlorine");
                        waterQuality.setChlorine(new BigDecimal(chlorine.toString()));
                    }
                    if (jsonObject.containsKey("turbidity")) {
                        Object turbidity = jsonObject.get("turbidity");
                        waterQuality.setTurbidity(new BigDecimal(turbidity.toString()));
                    }
                    waterQuality.setCollectTime(latestData.getCollectTime());

                    log.info("解析水质数据成功 - Temperature: {}, pH: {}, Chlorine: {}, Turbidity: {}",
                        waterQuality.getTemperature(), waterQuality.getPh(),
                        waterQuality.getChlorine(), waterQuality.getTurbidity());
                }
            } catch (Exception e) {
                log.warn("解析水质数据失败", e);
                // 设置默认值
                setDefaultWaterQuality(waterQuality);
            }
        } else {
            log.info("没有设备数据，使用默认值");
            // 没有数据时设置默认值
            setDefaultWaterQuality(waterQuality);
        }

        return waterQuality;
    }

    /**
     * 设置默认水质数据
     */
    private void setDefaultWaterQuality(DashboardStatisticsVo.WaterQualityData waterQuality) {
        waterQuality.setTemperature(new BigDecimal("26.5"));
        waterQuality.setPh(new BigDecimal("7.2"));
        waterQuality.setChlorine(new BigDecimal("0.6"));
        waterQuality.setTurbidity(new BigDecimal("1.5"));
        waterQuality.setCollectTime(LocalDateTime.now());
    }

    @Override
    public TrendDataVo getRechargeTrend() {
        TrendDataVo trendData = new TrendDataVo();
        List<String> dates = new ArrayList<>();
        List<Long> values = new ArrayList<>();

        // 查询近7天的充值数据
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);

            // 格式化日期为 MM/dd
            String dateStr = (date.getMonthValue() < 10 ? "0" : "") + date.getMonthValue() + "/" +
                           (date.getDayOfMonth() < 10 ? "0" : "") + date.getDayOfMonth();
            dates.add(dateStr);

            // 查询当天的充值订单总额
            List<SpOrder> dayOrders = orderMapper.selectList(
                new LambdaQueryWrapper<SpOrder>()
                    .eq(SpOrder::getOrderType, "2") // 2充值
                    .eq(SpOrder::getPayStatus, "1") // 1已支付
                    .ge(SpOrder::getCreateTime, dayStart)
                    .le(SpOrder::getCreateTime, dayEnd)
            );

            long dayIncome = dayOrders.stream()
                .mapToLong(order -> order.getAmount() != null ? order.getAmount() : 0L)
                .sum();
            values.add(dayIncome);
        }

        trendData.setDates(dates);
        trendData.setValues(values);
        return trendData;
    }

    @Override
    public TrendDataVo getMemberGrowthTrend() {
        TrendDataVo trendData = new TrendDataVo();
        List<String> dates = new ArrayList<>();
        List<Long> values = new ArrayList<>();

        // 查询近7天的新增会员数据
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);

            // 格式化日期为 MM/dd
            String dateStr = (date.getMonthValue() < 10 ? "0" : "") + date.getMonthValue() + "/" +
                           (date.getDayOfMonth() < 10 ? "0" : "") + date.getDayOfMonth();
            dates.add(dateStr);

            // 查询当天新增的会员数量
            Long count = memberCardMapper.selectCount(
                new LambdaQueryWrapper<SpMemberCard>()
                    .ge(SpMemberCard::getCreateTime, dayStart)
                    .le(SpMemberCard::getCreateTime, dayEnd)
            );
            values.add(count);
        }

        trendData.setDates(dates);
        trendData.setValues(values);
        return trendData;
    }
}
