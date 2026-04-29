package org.dromara.swimming.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.dromara.swimming.domain.vo.SpH5AnnouncementDetailVo;
import org.dromara.swimming.domain.vo.SpH5AnnouncementVo;
import org.dromara.swimming.service.ISpH5AnnouncementService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * H5公告控制器
 *
 * @author swimming
 * @date 2026-02-04
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/h5/announcement")
public class SpH5AnnouncementController extends BaseController {

    private final ISpH5AnnouncementService announcementService;

    /**
     * 查询最新公告列表
     *
     * @param limit 查询数量限制（可选，默认10条）
     * @return 公告列表
     */
    @SaCheckLogin
    @GetMapping("/list")
    public R<List<SpH5AnnouncementVo>> listLatestAnnouncements(
        @RequestParam(required = false, defaultValue = "10") Integer limit) {
        return announcementService.listLatestAnnouncements(limit);
    }

    /**
     * 查询公告详情
     *
     * @param announcementId 公告ID
     * @return 公告详情
     */
    @SaCheckLogin
    @GetMapping("/{announcementId}")
    public R<SpH5AnnouncementDetailVo> getAnnouncementDetail(@PathVariable Long announcementId) {
        return announcementService.getAnnouncementDetail(announcementId);
    }

    /**
     * 增加公告浏览次数
     *
     * @param announcementId 公告ID
     * @return 操作结果
     */
    @SaCheckLogin
    @PostMapping("/{announcementId}/view")
    public R<Void> incrementViewCount(@PathVariable Long announcementId) {
        return announcementService.incrementViewCount(announcementId);
    }
}
