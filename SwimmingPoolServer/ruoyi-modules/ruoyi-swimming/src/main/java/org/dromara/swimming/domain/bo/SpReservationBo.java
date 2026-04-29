package org.dromara.swimming.domain.bo;

import org.dromara.swimming.domain.SpReservation;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 预约记录业务对象 sp_reservation
 *
 * @author W
 * @date 2026-02-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SpReservation.class, reverseConvertGenerate = false)
public class SpReservationBo extends BaseEntity {

    /**
     * 预约ID
     */
    @NotNull(message = "预约ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 用户ID（关联sys_user.user_id）
     */
    @NotNull(message = "用户ID（关联sys_user.user_id）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 时段ID（关联sp_time_slot.id）
     */
    @NotNull(message = "时段ID（关联sp_time_slot.id）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long timeSlotId;

    /**
     * 泳道ID（关联sp_lane.id）
     */
    @NotNull(message = "泳道ID（关联sp_lane.id）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long laneId;

    /**
     * 预约单号
     */
    @NotBlank(message = "预约单号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String reservationNo;

    /**
     * 预约日期
     */
    @NotNull(message = "预约日期不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date reservationDate;

    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date startTime;

    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date endTime;

    /**
     * 状态（0待支付 1已支付 2已完成 3已取消 4已退款）
     */
    private String status;

    /**
     * 金额（元）
     */
    @NotNull(message = "金额（元）不能为空", groups = { AddGroup.class, EditGroup.class })
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


}
