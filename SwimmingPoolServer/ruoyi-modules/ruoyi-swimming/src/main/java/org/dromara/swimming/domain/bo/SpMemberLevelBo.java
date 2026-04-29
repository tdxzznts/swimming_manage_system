package org.dromara.swimming.domain.bo;

import org.dromara.swimming.domain.SpMemberLevel;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 会员等级配置业务对象 sp_member_level
 *
 * @author W
 * @date 2026-02-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SpMemberLevel.class, reverseConvertGenerate = false)
public class SpMemberLevelBo extends BaseEntity {

    /**
     * 主键ID
     */
    @NotNull(message = "主键ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 等级编码(normal/silver/gold/diamond)
     */
    @NotBlank(message = "等级编码(normal/silver/gold/diamond)不能为空", groups = { AddGroup.class, EditGroup.class })
    private String levelCode;

    /**
     * 等级名称
     */
    @NotBlank(message = "等级名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String levelName;

    /**
     * 英文名称
     */
    private String levelEn;

    /**
     * 等级值(0-3)
     */
    @NotNull(message = "等级值(0-3)不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long levelValue;

    /**
     * 折扣比例(如95表示95折)
     */
    private Long discount;

    /**
     * 升级阈值
     */
    private Long price;

    /**
     * 卡片渐变起始色
     */
    private String cardColorStart;

    /**
     * 卡片渐变结束色
     */
    private String cardColorEnd;

    /**
     * 等级描述
     */
    private String description;

    /**
     * 状态(0正常 1停用)
     */
    private String status;

    /**
     * 排序
     */
    private Long sortOrder;

    /**
     * 备注
     */
    private String remark;


}
