package org.dromara.swimming.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import java.util.List;

import java.io.Serial;

/**
 * 反馈对象 sp_feedback
 *
 * @author W
 * @date 2026-02-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sp_feedback")
public class SpFeedback extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 反馈ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 用户ID（关联sys_user.user_id）
     */
    private Long userId;

    /**
     * 反馈类型（1建议 2投诉）
     */
    private String feedbackType;

    /**
     * 反馈标题
     */
    private String title;

    /**
     * 反馈内容
     */
    private String content;

    /**
     * 图片URL（JSON数组）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
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

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;


}
