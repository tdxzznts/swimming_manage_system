package org.dromara.swimming.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * H5用户统计数据VO
 *
 * @author swimming
 * @date 2026-02-04
 */
@Data
public class SpH5UserStatisticsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 预约次数
     */
    private Integer reservationCount;

    /**
     * 游泳时长（小时）
     */
    private Double totalHours;

    /**
     * 账户余额（元）
     */
    private Long balance;

    /**
     * 积分
     */
    private Long totalPoints;
}
