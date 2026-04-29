package org.dromara.swimming.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 首页统计数据VO
 *
 * @author swimming
 * @date 2026-02-09
 */
@Data
public class DashboardStatisticsVo {

    /**
     * 会员统计
     */
    private MemberStatistics memberStats;

    /**
     * 预约统计
     */
    private ReservationStatistics reservationStats;

    /**
     * 订单统计
     */
    private OrderStatistics orderStats;

    /**
     * 设备统计
     */
    private DeviceStatistics deviceStats;

    /**
     * 水质实时数据
     */
    private WaterQualityData waterQuality;

    /**
     * 数据更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Data
    public static class MemberStatistics {
        /**
         * 总会员数
         */
        private Integer totalMembers;

        /**
         * 今日新增会员数
         */
        private Integer todayNewMembers;

        /**
         * 会员总余额
         */
        private BigDecimal totalBalance;
    }

    @Data
    public static class ReservationStatistics {
        /**
         * 今日预约数
         */
        private Integer todayReservations;

        /**
         * 当前在场人数
         */
        private Integer currentPresent;
    }

    @Data
    public static class OrderStatistics {
        /**
         * 今日订单数
         */
        private Integer todayOrders;

        /**
         * 今日收入
         */
        private BigDecimal todayIncome;

        /**
         * 本月收入
         */
        private BigDecimal monthIncome;
    }

    @Data
    public static class DeviceStatistics {
        /**
         * 设备总数
         */
        private Integer totalDevices;

        /**
         * 在线设备数
         */
        private Integer onlineDevices;

        /**
         * 离线设备数
         */
        private Integer offlineDevices;

        /**
         * 今日告警数
         */
        private Integer todayAlarms;
    }

    @Data
    public static class WaterQualityData {
        /**
         * 温度
         */
        private BigDecimal temperature;

        /**
         * pH值
         */
        private BigDecimal ph;

        /**
         * 余氯
         */
        private BigDecimal chlorine;

        /**
         * 浊度
         */
        private BigDecimal turbidity;

        /**
         * 更新时间
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime collectTime;
    }
}
