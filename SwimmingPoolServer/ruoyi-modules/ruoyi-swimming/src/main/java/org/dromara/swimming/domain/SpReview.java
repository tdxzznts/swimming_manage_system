package org.dromara.swimming.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 评价对象 sp_review
 *
 * @author W
 * @date 2026-02-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sp_review")
public class SpReview extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 评价ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 用户ID（关联sys_user.user_id）
     */
    private Long userId;

    /**
     * 预约ID（关联sp_reservation.id）
     */
    private Long reservationId;

    /**
     * 评分（1-5星）
     */
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

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;


}
