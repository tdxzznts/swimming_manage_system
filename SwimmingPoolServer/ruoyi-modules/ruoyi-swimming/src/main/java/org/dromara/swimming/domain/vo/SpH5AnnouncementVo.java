package org.dromara.swimming.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * H5公告VO
 *
 * @author swimming
 * @date 2026-02-04
 */
@Data
public class SpH5AnnouncementVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 公告ID
     */
    private Long id;

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告类型（1系统公告 2活动通知 3紧急通知）
     */
    private String announcementType;

    /**
     * 公告类型名称
     */
    private String announcementTypeName;

    /**
     * 发布时间
     */
    private Date publishTime;

    /**
     * 浏览次数
     */
    private Long viewCount;
}
