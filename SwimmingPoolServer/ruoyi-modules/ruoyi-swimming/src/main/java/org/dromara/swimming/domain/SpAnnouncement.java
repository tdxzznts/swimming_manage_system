package org.dromara.swimming.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 系统公告对象 sp_announcement
 *
 * @author W
 * @date 2026-02-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sp_announcement")
public class SpAnnouncement extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 公告ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 公告类型（1系统公告 2活动通知 3紧急通知）
     */
    private String announcementType;

    /**
     * 发布时间
     */
    private Date publishTime;

    /**
     * 状态（0草稿 1已发布）
     */
    private String status;

    /**
     * 浏览次数
     */
    private Long viewCount;

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
