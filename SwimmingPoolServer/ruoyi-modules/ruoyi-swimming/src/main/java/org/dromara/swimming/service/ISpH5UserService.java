package org.dromara.swimming.service;

import org.dromara.common.core.domain.R;
import org.dromara.swimming.domain.vo.SpH5UserStatisticsVo;

/**
 * H5用户服务接口
 *
 * @author swimming
 * @date 2026-02-04
 */
public interface ISpH5UserService {

    /**
     * 获取用户统计数据
     *
     * @return 统计数据
     */
    R<SpH5UserStatisticsVo> getUserStatistics();
}
