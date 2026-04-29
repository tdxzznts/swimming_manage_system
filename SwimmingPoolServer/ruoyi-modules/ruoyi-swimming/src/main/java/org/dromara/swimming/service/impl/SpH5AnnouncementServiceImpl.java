package org.dromara.swimming.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.utils.DateUtils;
import org.dromara.swimming.domain.SpAnnouncement;
import org.dromara.swimming.domain.vo.SpH5AnnouncementDetailVo;
import org.dromara.swimming.domain.vo.SpH5AnnouncementVo;
import org.dromara.swimming.mapper.SpAnnouncementMapper;
import org.dromara.swimming.service.ISpH5AnnouncementService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * H5公告服务实现
 *
 * @author swimming
 * @date 2026-02-04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SpH5AnnouncementServiceImpl implements ISpH5AnnouncementService {

    private final SpAnnouncementMapper announcementMapper;

    // 状态常量
    private static final String STATUS_PUBLISHED = "1";

    // 公告类型字典
    private static final String ANNOUNCEMENT_TYPE_SYSTEM = "1";
    private static final String ANNOUNCEMENT_TYPE_ACTIVITY = "2";
    private static final String ANNOUNCEMENT_TYPE_URGENT = "3";

    @Override
    public R<List<SpH5AnnouncementVo>> listLatestAnnouncements(Integer limit) {
        try {
            // 查询已发布的公告
            LambdaQueryWrapper<SpAnnouncement> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SpAnnouncement::getStatus, STATUS_PUBLISHED);
            wrapper.eq(SpAnnouncement::getDelFlag, "0");
            wrapper.orderByDesc(SpAnnouncement::getPublishTime);

            // 限制查询数量
            if (limit != null && limit > 0) {
                wrapper.last("LIMIT " + limit);
            }

            List<SpAnnouncement> announcements = announcementMapper.selectList(wrapper);

            // 转换为VO
            List<SpH5AnnouncementVo> voList = announcements.stream().map(announcement -> {
                SpH5AnnouncementVo vo = new SpH5AnnouncementVo();
                vo.setId(announcement.getId());
                vo.setTitle(announcement.getTitle());
                vo.setAnnouncementType(announcement.getAnnouncementType());
                vo.setAnnouncementTypeName(getAnnouncementTypeName(announcement.getAnnouncementType()));
                vo.setPublishTime(announcement.getPublishTime());
                vo.setViewCount(announcement.getViewCount() != null ? announcement.getViewCount() : 0L);
                return vo;
            }).toList();

            return R.ok(voList);
        } catch (Exception e) {
            log.error("查询最新公告失败", e);
            return R.fail("查询公告失败");
        }
    }

    @Override
    public R<SpH5AnnouncementDetailVo> getAnnouncementDetail(Long announcementId) {
        try {
            SpAnnouncement announcement = announcementMapper.selectById(announcementId);
            if (announcement == null) {
                return R.fail("公告不存在");
            }

            // 转换为详情VO
            SpH5AnnouncementDetailVo vo = new SpH5AnnouncementDetailVo();
            vo.setId(announcement.getId());
            vo.setTitle(announcement.getTitle());
            vo.setContent(announcement.getContent());
            vo.setAnnouncementType(announcement.getAnnouncementType());
            vo.setAnnouncementTypeName(getAnnouncementTypeName(announcement.getAnnouncementType()));
            vo.setPublishTime(announcement.getPublishTime());
            vo.setViewCount(announcement.getViewCount() != null ? announcement.getViewCount() : 0L);

            return R.ok(vo);
        } catch (Exception e) {
            log.error("查询公告详情失败，公告ID={}", announcementId, e);
            return R.fail("查询公告详情失败");
        }
    }

    @Override
    public R<Void> incrementViewCount(Long announcementId) {
        try {
            SpAnnouncement announcement = announcementMapper.selectById(announcementId);
            if (announcement == null) {
                return R.fail("公告不存在");
            }

            // 增加浏览次数
            Long currentViewCount = announcement.getViewCount() != null ? announcement.getViewCount() : 0L;
            announcement.setViewCount(currentViewCount + 1);
            announcementMapper.updateById(announcement);

            return R.ok();
        } catch (Exception e) {
            log.error("增加浏览次数失败，公告ID={}", announcementId, e);
            return R.fail("增加浏览次数失败");
        }
    }

    /**
     * 获取公告类型名称
     */
    private String getAnnouncementTypeName(String announcementType) {
        return switch (announcementType) {
            case ANNOUNCEMENT_TYPE_SYSTEM -> "系统公告";
            case ANNOUNCEMENT_TYPE_ACTIVITY -> "活动通知";
            case ANNOUNCEMENT_TYPE_URGENT -> "紧急通知";
            default -> "公告";
        };
    }
}
