package org.dromara.swimming.service;

import org.dromara.swimming.domain.vo.DashboardStatisticsVo;
import org.dromara.swimming.domain.vo.TrendDataVo;

/**
 * 首页统计服务接口
 *
 * @author swimming
 * @date 2026-02-09
 */
public interface IDashboardService {

    /**
     * 获取首页统计数据
     *
     * @return 统计数据
     */
    DashboardStatisticsVo getDashboardStatistics();

    /**
     * 获取充值收入趋势（近7天）
     *
     * @return 趋势数据
     */
    TrendDataVo getRechargeTrend();

    /**
     * 获取会员增长趋势（近7天）
     *
     * @return 趋势数据
     */
    TrendDataVo getMemberGrowthTrend();
}
