package org.dromara.swimming.domain.bo;

import org.dromara.swimming.domain.SpFeedback;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 反馈业务对象 sp_feedback
 *
 * @author W
 * @date 2026-02-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SpFeedback.class, reverseConvertGenerate = false)
public class SpFeedbackBo extends BaseEntity {

    /**
     * 反馈ID
     */
    @NotNull(message = "反馈ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 用户ID（关联sys_user.user_id）
     */
    @NotNull(message = "用户ID（关联sys_user.user_id）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 用户名称（非数据库字段，用于查询）
     */
    private String userName;

    /**
     * 反馈类型（1建议 2投诉）
     */
    private String feedbackType;

    /**
     * 反馈标题
     */
    @NotBlank(message = "反馈标题不能为空", groups = { AddGroup.class, EditGroup.class })
    private String title;

    /**
     * 反馈内容
     */
    @NotBlank(message = "反馈内容不能为空", groups = { AddGroup.class, EditGroup.class })
    private String content;

    /**
     * 图片URL（JSON数组）
     */
    private List<String> images;

    /**
     * 联系方式
     */
    private String contactInfo;

    /**
     * 状态（0待处理 1处理中 2已完成）
     */
    private String status;

    /**
     * 处理人ID
     */
    private Long handleUserId;

    /**
     * 处理结果
     */
    private String handleResult;

    /**
     * 处理时间
     */
    private Date handleTime;

    /**
     * 满意度评分（1-5）
     */
    private Integer satisfaction;

    /**
     * 备注
     */
    private String remark;


}
