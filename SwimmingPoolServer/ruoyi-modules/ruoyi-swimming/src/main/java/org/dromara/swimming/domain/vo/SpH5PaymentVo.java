package org.dromara.swimming.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * H5支付信息VO
 *
 * @author swimming
 * @date 2026-02-04
 */
@Data
public class SpH5PaymentVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 预约ID
     */
    private Long reservationId;

    /**
     * 订单金额（元）
     */
    private Long amount;

    /**
     * 会员折扣（例如：95表示95折）
     */
    private Integer discount;

    /**
     * 优惠金额（元）
     */
    private Long discountAmount;

    /**
     * 实际支付金额（元）
     */
    private Long actualAmount;

    /**
     * 获得积分（元）
     */
    private Long getPoint;

    /**
     * 当前余额（元）
     */
    private Long balance;

    /**
     * 积分余额
     */
    private Long totalPoint;

    /**
     * 会员卡等级
     */
    private String cardLevel;

    /**
     * 会员卡等级名称
     */
    private String cardLevelName;

    /**
     * 剩余免费次数
     */
    private Integer freeTimes;

    /**
     * 积分倍数（权益表POINTS_DOUBLE的值）
     */
    private Integer pointsDouble;

    /**
     * 预约折扣（权益表BOOKING_DISCOUNT的值，如95表示95折）
     */
    private Integer bookingDiscount;
}
