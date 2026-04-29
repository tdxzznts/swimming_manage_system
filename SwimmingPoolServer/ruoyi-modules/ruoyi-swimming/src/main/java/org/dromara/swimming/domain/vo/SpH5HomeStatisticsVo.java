package org.dromara.swimming.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * H5首页统计信息VO
 *
 * @author swimming
 * @date 2026-02-04
 */
@Data
public class SpH5HomeStatisticsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 今日剩余时间段数量
     */
    private Integer remainingSlots;

    /**
     * 当前在场人数
     */
    private Integer currentPeopleCount;
}
