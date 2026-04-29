package org.dromara.swimming.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 订单对象 sp_order
 *
 * @author W
 * @date 2026-02-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sp_order")
public class SpOrder extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户ID（关联sys_user.user_id）
     */
    private Long userId;

    /**
     * 用户名（非数据库字段，用于展示）
     */
    @TableField(exist = false)
    private String userName;

    /**
     * 预约ID（关联sp_reservation.id）
     */
    private Long reservationId;

    /**
     * 订单类型（1预约 2充值 3积分）
     */
    private String orderType;

    /**
     * 订单金额（元）
     */
    private Long amount;

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
     * 支付方式（1支付宝 2余额）
     */
    private String payType;

    /**
     * 支付状态（0待支付 1已支付 2支付失败 3已退款）
     */
    private String payStatus;

    /**
     * 支付时间
     */
    private Date payTime;

    /**
     * 第三方交易号
     */
    private String tradeNo;

    /**
     * 第三方订单号
     */
    private String thirdPartyNo;

    /**
     * 退款金额（元）
     */
    private Long refundAmount;

    /**
     * 退款时间
     */
    private Date refundTime;

    /**
     * 退款原因
     */
    private String refundReason;

    /**
     * 订单状态（0待支付 1已支付 2已完成 3已取消 4已退款）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;


}
