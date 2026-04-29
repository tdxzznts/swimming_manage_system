package org.dromara.swimming.domain.vo;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.swimming.domain.SpFeedback;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;



/**
 * 反馈视图对象 sp_feedback
 *
 * @author W
 * @date 2026-02-05
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SpFeedback.class)
public class SpFeedbackVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 反馈ID
     */
    @ExcelProperty(value = "反馈ID")
    private Long id;

    /**
     * 用户ID（关联sys_user.user_id）
     */
    @ExcelProperty(value = "用户ID", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "关=联sys_user.user_id")
    private Long userId;

    /**
     * 用户名称（非数据库字段，用于显示）
     */
    private String userName;

    /**
     * 处理人名称（非数据库字段，用于显示）
     */
    private String handleUserName;

    /**
     * 反馈类型（1建议 2投诉）
     */
    @ExcelProperty(value = "反馈类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sp_feedback_type")
    private String feedbackType;

    /**
     * 反馈标题
     */
    @ExcelProperty(value = "反馈标题")
    private String title;

    /**
     * 反馈内容
     */
    @ExcelProperty(value = "反馈内容")
    private String content;

    /**
     * 图片URL（JSON数组）
     */
    private List<String> images;

    /**
     * 联系方式
     */
    @ExcelProperty(value = "联系方式")
    private String contactInfo;

    /**
     * 状态（0待处理 1处理中 2已完成）
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sp_feedback_status")
    private String status;

    /**
     * 处理人ID
     */
    @ExcelProperty(value = "处理人ID")
    private Long handleUserId;

    /**
     * 处理结果
     */
    @ExcelProperty(value = "处理结果")
    private String handleResult;

    /**
     * 处理时间
     */
    @ExcelProperty(value = "处理时间")
    private Date handleTime;

    /**
     * 满意度评分（1-5）
     */
    private Integer satisfaction;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "创建时间")
    private Date createTime;

}
