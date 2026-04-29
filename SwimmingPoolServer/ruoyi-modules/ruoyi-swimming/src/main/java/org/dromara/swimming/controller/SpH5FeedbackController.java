package org.dromara.swimming.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.dromara.swimming.domain.bo.SpFeedbackBo;
import org.dromara.swimming.domain.vo.SpFeedbackVo;
import org.dromara.swimming.service.ISpFeedbackService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * H5反馈控制器
 *
 * @author swimming
 * @date 2026-02-07
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/h5/feedback")
public class SpH5FeedbackController extends BaseController {

    private final ISpFeedbackService feedbackService;

    /**
     * 创建反馈
     *
     * @param bo 反馈业务对象
     * @return 创建结果
     */
    @SaCheckLogin
    @PostMapping
    public R<Void> createFeedback(@Validated @RequestBody SpFeedbackBo bo) {

        // 设置当前登录用户ID
        bo.setUserId(LoginHelper.getUserId());

        // 调用服务创建反馈
        return feedbackService.insertByBo(bo) ? R.ok() : R.fail();
    }

    /**
     * 获取我的反馈列表
     *
     * @param status 反馈状态（可选）
     * @param pageNum 页码（默认1）
     * @param pageSize 每页大小（默认10）
     * @return 反馈列表
     */
    @SaCheckLogin
    @GetMapping("/my")
    public R<TableDataInfo<SpFeedbackVo>> getMyFeedbackList(
        @RequestParam(required = false) String status,
        @RequestParam(defaultValue = "1") Integer pageNum,
        @RequestParam(defaultValue = "10") Integer pageSize) {

        // 创建查询条件
        SpFeedbackBo bo = new SpFeedbackBo();
        Long userId = LoginHelper.getUserId();
        bo.setUserId(userId);
        if (StringUtils.isNotEmpty(status)) {
            bo.setStatus(status);
        }

        // 设置分页参数
        PageQuery pageQuery = new PageQuery(pageSize, pageNum);

        // 查询反馈列表
        TableDataInfo<SpFeedbackVo> list = feedbackService.queryPageList(bo, pageQuery);

        // 调试日志
        System.out.println("=== Debug Info ===");
        System.out.println("UserId: " + userId);
        System.out.println("PageNum: " + pageNum + ", PageSize: " + pageSize);
        System.out.println("Total: " + list.getTotal());
        System.out.println("Rows size: " + list.getRows().size());
        if (list.getRows() != null && !list.getRows().isEmpty()) {
            for (SpFeedbackVo vo : list.getRows()) {
                System.out.println("  - ID: " + vo.getId() + ", Title: " + vo.getTitle());
            }
        }

        return R.ok(list);
    }

    /**
     * 获取反馈详情
     *
     * @param feedbackId 反馈ID
     * @return 反馈详情
     */
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<SpFeedbackVo> getFeedbackDetail(@PathVariable Long id) {
        // 查询反馈详情
        SpFeedbackVo feedback = feedbackService.queryById(id);
        if (feedback == null) {
            return R.fail("反馈不存在");
        }

        // 检查权限：只能查看自己的反馈
        if (!feedback.getUserId().equals(LoginHelper.getUserId())) {
            return R.fail("无权查看该反馈");
        }

        return R.ok(feedback);
    }
}
