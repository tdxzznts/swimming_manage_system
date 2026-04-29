package org.dromara.swimming.controller;

import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.dromara.swimming.domain.vo.DashboardStatisticsVo;
import org.dromara.swimming.domain.vo.TrendDataVo;
import org.dromara.swimming.service.IDashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 游泳馆首页统计Controller
 *
 * @author swimming
 * @date 2026-02-09
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/swimming/dashboard")
public class SwimmingDashboardController extends BaseController {

    private final IDashboardService dashboardService;

    /**
     * 获取首页统计数据
     */
    @GetMapping("/statistics")
    public R<DashboardStatisticsVo> getStatistics() {
        return R.ok(dashboardService.getDashboardStatistics());
    }

    /**
     * 获取充值收入趋势
     */
    @GetMapping("/recharge-trend")
    public R<TrendDataVo> getRechargeTrend() {
        return R.ok(dashboardService.getRechargeTrend());
    }

    /**
     * 获取会员增长趋势
     */
    @GetMapping("/member-growth-trend")
    public R<TrendDataVo> getMemberGrowthTrend() {
        return R.ok(dashboardService.getMemberGrowthTrend());
    }
}
