package org.dromara.swimming.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * 趋势数据VO
 *
 * @author swimming
 * @date 2026-02-09
 */
@Data
public class TrendDataVo {

    /**
     * 日期列表
     */
    private List<String> dates;

    /**
     * 数值列表
     */
    private List<Long> values;
}
