package org.dromara.swimming.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 泳道对象 sp_lane
 *
 * @author W
 * @date 2026-02-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sp_lane")
public class SpLane extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 泳道ID
     */
    @TableId(value = "id")
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
     * 容量（人数）
     */
    private Long capacity;

    /**
     * 泳道长度（米）
     */
    private Long length;

    /**
     * 泳道宽度（米）
     */
    private Long width;

    /**
     * 泳道深度（米）
     */
    private Long depth;

    /**
     * 状态（0开放 1关闭 2维修）
     */
    private String status;

    /**
     * 排序
     */
    private Long sort;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;


}
