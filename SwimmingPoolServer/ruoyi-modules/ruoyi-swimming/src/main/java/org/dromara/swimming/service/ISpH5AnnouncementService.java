package org.dromara.swimming.service;

import org.dromara.common.core.domain.R;
import org.dromara.swimming.domain.vo.SpH5AnnouncementDetailVo;
import org.dromara.swimming.domain.vo.SpH5AnnouncementVo;

import java.util.List;

/**
 * H5公告服务接口
 *
 * @author swimming
 * @date 2026-02-04
 */
public interface ISpH5AnnouncementService {

    /**
     * 查询最新公告列表
     *
     * @param limit 查询数量限制
     * @return 公告列表
     */
    R<List<SpH5AnnouncementVo>> listLatestAnnouncements(Integer limit);

    /**
     * 查询公告详情
     *
     * @param announcementId 公告ID
     * @return 公告详情
     */
    R<SpH5AnnouncementDetailVo> getAnnouncementDetail(Long announcementId);

    /**
     * 增加公告浏览次数
     *
     * @param announcementId 公告ID
     * @return 操作结果
     */
    R<Void> incrementViewCount(Long announcementId);
}
