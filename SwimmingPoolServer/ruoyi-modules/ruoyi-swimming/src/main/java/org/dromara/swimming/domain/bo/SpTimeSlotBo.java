package org.dromara.swimming.domain.bo;

import org.dromara.swimming.domain.SpTimeSlot;
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
 * 预约时段业务对象 sp_time_slot
 *
 * @author W
 * @date 2026-02-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SpTimeSlot.class, reverseConvertGenerate = false)
public class SpTimeSlotBo extends BaseEntity {

    /**
     * 时段ID
     */
    @NotNull(message = "时段ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 泳道ID（关联sp_lane.id）
     */
    @NotNull(message = "泳道ID（关联sp_lane.id）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long laneId;

    /**
     * 日期
     */
    @NotNull(message = "日期不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date slotDate;

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
     * 时段类型（1高峰期 2低峰期）
     */
    private String slotType;

    /**
     * 容量（人数）
     */
    @NotNull(message = "容量（人数）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long capacity;

    /**
     * 已预约人数
     */
    private Long bookedCount;

    /**
     * 单价（元）
     */
    @NotNull(message = "单价（元）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long price;

    /**
     * 是否可预约（0否 1是）
     */
    private String isAvailable;

    /**
     * 备注
     */
    private String remark;


}
