package org.dromara.swimming.domain.bo;

import org.dromara.swimming.domain.SpLane;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 泳道业务对象 sp_lane
 *
 * @author W
 * @date 2026-02-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SpLane.class, reverseConvertGenerate = false)
public class SpLaneBo extends BaseEntity {

    /**
     * 泳道ID
     */
    @NotNull(message = "泳道ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 泳道编号
     */
    @NotBlank(message = "泳道编号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String laneNo;

    /**
     * 泳道类型（1快速 2中速 3慢速）
     */
    private String laneType;

    /**
     * 容量（人数）
     */
    @NotNull(message = "容量（人数）不能为空", groups = { AddGroup.class, EditGroup.class })
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


}
