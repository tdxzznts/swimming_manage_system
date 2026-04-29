package org.dromara.swimming.domain.vo;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * H5时段信息展示对象
 *
 * @author swimming
 * @date 2026-02-03
 */
@Data
public class SpH5TimeSlotVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 时段ID
     */
    private Long id;

    /**
     * 泳道ID
     */
    private Long laneId;

    /**
     * 泳道编号
     */
    private String laneNo;

    /**
     * 泳道类型
     */
    private String laneType;

    /**
     * 日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date slotDate;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "HH:mm")
    private Date startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "HH:mm")
    private Date endTime;

    /**
     * 时段类型（1高峰期 2低峰期）
     */
    private String slotType;

    /**
     * 时段类型名称
     */
    private String slotTypeName;

    /**
     * 容量（人数）
     */
    private Integer capacity;

    /**
     * 已预约人数
     */
    private Integer bookedCount;

    /**
     * 剩余名额
     */
    private Integer remainingCount;

    /**
     * 单价（元）
     */
    private Long price;

    /**
     * 是否可预约（0否 1是）
     */
    private String isAvailable;
}
