package org.dromara.swimming.domain.vo;

import org.dromara.swimming.domain.SpMemberBenefit;
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
 * 会员权益配置视图对象 sp_member_benefit
 *
 * @author W
 * @date 2026-02-05
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SpMemberBenefit.class)
public class SpMemberBenefitVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ExcelProperty(value = "主键ID")
    private Long id;

    /**
     * 权益编码
     */
    @ExcelProperty(value = "权益编码")
    private String benefitCode;

    /**
     * 权益名称
     */
    @ExcelProperty(value = "权益名称")
    private String benefitName;

    /**
     * 权益描述
     */
    @ExcelProperty(value = "权益描述")
    private String benefitDesc;

    /**
     * 图标名称
     */
    @ExcelProperty(value = "图标名称")
    private String iconName;

    /**
     * 权益值(如"95折","3天")
     */
    @ExcelProperty(value = "权益值")
    private String benefitValue;

    /**
     * 标签类型(success/warning/danger/info)
     */
    @ExcelProperty(value = "标签类型(success/warning/danger/info)")
    private String tagType;

    /**
     * 适用等级(1、2、3)
     */
    @ExcelProperty(value = "适用等级(1、2、3)", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sp_member_level")
    private String levelValue;

    /**
     * 排序
     */
    @ExcelProperty(value = "排序")
    private Long sortOrder;

    /**
     * 状态(0正常 1停用)
     */
    @ExcelProperty(value = "状态(0正常 1停用)")
    private String status;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
