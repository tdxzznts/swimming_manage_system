package org.dromara.swimming.domain.bo;

import org.dromara.swimming.domain.SpAnnouncement;
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
 * 系统公告业务对象 sp_announcement
 *
 * @author W
 * @date 2026-02-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SpAnnouncement.class, reverseConvertGenerate = false)
public class SpAnnouncementBo extends BaseEntity {

    /**
     * 公告ID
     */
    @NotNull(message = "公告ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 公告标题
     */
    @NotBlank(message = "公告标题不能为空", groups = { AddGroup.class, EditGroup.class })
    private String title;

    /**
     * 公告内容
     */
    @NotBlank(message = "公告内容不能为空", groups = { AddGroup.class, EditGroup.class })
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


}
