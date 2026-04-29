package org.dromara.swimming.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.swimming.domain.SpTimeSlot;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 预约时段视图对象 sp_time_slot
 *
 * @author W
 * @date 2026-02-05
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SpTimeSlot.class)
public class SpTimeSlotVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 时段ID
     */
    @ExcelProperty(value = "时段ID")
    private Long id;

    /**
     * 泳道ID（关联sp_lane.id）
     */
    @ExcelProperty(value = "泳道ID", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "关=联sp_lane.id")
    private Long laneId;

    /**
     * 日期
     */
    @ExcelProperty(value = "日期")
    private Date slotDate;

    /**
     * 开始时间
     */
    @ExcelProperty(value = "开始时间")
    private Date startTime;

    /**
     * 结束时间
     */
    @ExcelProperty(value = "结束时间")
    private Date endTime;

    /**
     * 时段类型（1高峰期 2低峰期）
     */
    @ExcelProperty(value = "时段类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sp_slot_type")
    private String slotType;

    /**
     * 容量（人数）
     */
    @ExcelProperty(value = "容量", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "人=数")
    private Long capacity;

    /**
     * 已预约人数
     */
    @ExcelProperty(value = "已预约人数")
    private Long bookedCount;

    /**
     * 单价（元）
     */
    @ExcelProperty(value = "单价", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "元=")
    private Long price;

    /**
     * 是否可预约（0否 1是）
     */
    @ExcelProperty(value = "是否可预约", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=否,1=是")
    private String isAvailable;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
