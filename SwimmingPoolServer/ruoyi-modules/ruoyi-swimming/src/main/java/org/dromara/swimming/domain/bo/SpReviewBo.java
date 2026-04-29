package org.dromara.swimming.domain.bo;

import org.dromara.swimming.domain.SpReview;
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
 * 评价业务对象 sp_review
 *
 * @author W
 * @date 2026-02-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SpReview.class, reverseConvertGenerate = false)
public class SpReviewBo extends BaseEntity {

    /**
     * 评价ID
     */
    @NotNull(message = "评价ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 用户ID（关联sys_user.user_id）
     */
    @NotNull(message = "用户ID（关联sys_user.user_id）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 预约ID（关联sp_reservation.id）
     */
    private Long reservationId;

    /**
     * 评分（1-5星）
     */
    @NotNull(message = "评分（1-5星）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long rating;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 图片URL（JSON数组）
     */
    private String images;

    /**
     * 状态（0待审核 1已通过 2已拒绝）
     */
    private String status;

    /**
     * 管理员回复
     */
    private String reply;

    /**
     * 回复人ID
     */
    private Long replyUserId;

    /**
     * 回复时间
     */
    private Date replyTime;

    /**
     * 备注
     */
    private String remark;


}
