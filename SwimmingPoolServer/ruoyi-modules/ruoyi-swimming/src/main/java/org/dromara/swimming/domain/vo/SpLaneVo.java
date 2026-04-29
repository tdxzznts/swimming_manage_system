package org.dromara.swimming.domain.vo;

import org.dromara.swimming.domain.SpLane;
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
 * 泳道视图对象 sp_lane
 *
 * @author W
 * @date 2026-02-05
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SpLane.class)
public class SpLaneVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 泳道ID
     */
    @ExcelProperty(value = "泳道ID")
    private Long id;

    /**
     * 泳道编号
     */
    @ExcelProperty(value = "泳道编号")
    private String laneNo;

    /**
     * 泳道类型（1快速 2中速 3慢速）
     */
    @ExcelProperty(value = "泳道类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sp_lane_type")
    private String laneType;

    /**
     * 容量（人数）
     */
    @ExcelProperty(value = "容量", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "人=数")
    private Long capacity;

    /**
     * 泳道长度（米）
     */
    @ExcelProperty(value = "泳道长度", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "米=")
    private Long length;

    /**
     * 泳道宽度（米）
     */
    @ExcelProperty(value = "泳道宽度", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "米=")
    private Long width;

    /**
     * 泳道深度（米）
     */
    @ExcelProperty(value = "泳道深度", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "米=")
    private Long depth;

    /**
     * 状态（0开放 1关闭 2维修）
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sp_lane_status")
    private String status;

    /**
     * 排序
     */
    @ExcelProperty(value = "排序")
    private Long sort;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
