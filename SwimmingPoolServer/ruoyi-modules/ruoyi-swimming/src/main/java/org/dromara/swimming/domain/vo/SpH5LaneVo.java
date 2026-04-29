package org.dromara.swimming.domain.vo;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * H5泳道信息展示对象
 *
 * @author swimming
 * @date 2026-02-03
 */
@Data
public class SpH5LaneVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 泳道ID
     */
    private Long id;

    /**
     * 泳道编号
     */
    private String laneNo;

    /**
     * 泳道类型（1快速 2中速 3慢速）
     */
    private String laneType;

    /**
     * 泳道类型名称
     */
    private String laneTypeName;

    /**
     * 容量（人数）
     */
    private Integer capacity;

    /**
     * 泳道长度（米）
     */
    private Integer length;

    /**
     * 泳道宽度（米）
     */
    private Integer width;

    /**
     * 泳道深度（米）
     */
    private Integer depth;

    /**
     * 状态（0开放 1关闭 2维修）
     */
    private String status;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 排序
     */
    private Integer sort;
}
