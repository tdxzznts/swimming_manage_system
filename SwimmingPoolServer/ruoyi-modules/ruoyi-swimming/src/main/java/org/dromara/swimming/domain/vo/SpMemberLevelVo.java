package org.dromara.swimming.domain.vo;

import org.dromara.swimming.domain.SpMemberLevel;
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
 * 会员等级配置视图对象 sp_member_level
 *
 * @author W
 * @date 2026-02-05
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SpMemberLevel.class)
public class SpMemberLevelVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ExcelProperty(value = "主键ID")
    private Long id;

    /**
     * 等级编码(normal/silver/gold/diamond)
     */
    @ExcelProperty(value = "等级编码(normal/silver/gold/diamond)")
    private String levelCode;

    /**
     * 等级名称
     */
    @ExcelProperty(value = "等级名称")
    private String levelName;

    /**
     * 英文名称
     */
    @ExcelProperty(value = "英文名称")
    private String levelEn;

    /**
     * 等级值(0-3)
     */
    @ExcelProperty(value = "等级值(0-3)", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sp_member_level")
    private Long levelValue;

    /**
     * 折扣比例(如95表示95折)
     */
    @ExcelProperty(value = "折扣比例(如95表示95折)")
    private Long discount;

    /**
     * 升级阈值
     */
    @ExcelProperty(value = "升级阈值")
    private Long price;

    /**
     * 卡片渐变起始色
     */
    @ExcelProperty(value = "卡片渐变起始色")
    private String cardColorStart;

    /**
     * 卡片渐变结束色
     */
    @ExcelProperty(value = "卡片渐变结束色")
    private String cardColorEnd;

    /**
     * 等级描述
     */
    @ExcelProperty(value = "等级描述")
    private String description;

    /**
     * 状态(0正常 1停用)
     */
    @ExcelProperty(value = "状态(0正常 1停用)")
    private String status;

    /**
     * 排序
     */
    @ExcelProperty(value = "排序")
    private Long sortOrder;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
