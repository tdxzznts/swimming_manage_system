package org.dromara.swimming.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 预约记录对象 sp_reservation
 *
 * @author W
 * @date 2026-02-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sp_reservation")
public class SpReservation extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 预约ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 用户ID（关联sys_user.user_id）
     */
    private Long userId;

    /**
     * 时段ID（关联sp_time_slot.id）
     */
    private Long timeSlotId;

    /**
     * 泳道ID（关联sp_lane.id）
     */
    private Long laneId;

    /**
     * 预约单号
     */
    private String reservationNo;

    /**
     * 预约日期
     */
    private Date reservationDate;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 状态（0待支付 1已支付 2已完成 3已取消 4已退款）
     */
    private String status;

    /**
     * 金额（元）
     */
    private Long amount;

    /**
     * 订单ID（关联sp_order.id）
     */
    private Long orderId;

    /**
     * 签到时间
     */
    private Date checkInTime;

    /**
     * 签退时间
     */
    private Date checkOutTime;

    /**
     * 取消时间
     */
    private Date cancelTime;

    /**
     * 取消原因
     */
    private String cancelReason;

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
