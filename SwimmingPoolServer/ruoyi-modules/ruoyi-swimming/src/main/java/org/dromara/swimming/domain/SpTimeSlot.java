package org.dromara.swimming.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 预约时段对象 sp_time_slot
 *
 * @author W
 * @date 2026-02-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sp_time_slot")
public class SpTimeSlot extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 时段ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 泳道ID（关联sp_lane.id）
     */
    private Long laneId;

    /**
     * 日期
     */
    private Date slotDate;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 时段类型（1高峰期 2低峰期）
     */
    private String slotType;

    /**
     * 容量（人数）
     */
    private Long capacity;

    /**
     * 已预约人数
     */
    private Long bookedCount;

    /**
     * 单价（元）
     */
    private Long price;

    /**
     * 是否可预约（0否 1是）
     */
    private String isAvailable;

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
