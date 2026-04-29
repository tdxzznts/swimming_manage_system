package org.dromara.swimming.domain.bo;

import org.dromara.swimming.domain.SpMemberBenefit;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 会员权益配置业务对象 sp_member_benefit
 *
 * @author W
 * @date 2026-02-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SpMemberBenefit.class, reverseConvertGenerate = false)
public class SpMemberBenefitBo extends BaseEntity {

    /**
     * 主键ID
     */
    @NotNull(message = "主键ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 权益编码
     */
    private String benefitCode;

    /**
     * 权益名称
     */
    private String benefitName;

    /**
     * 权益描述
     */
    private String benefitDesc;

    /**
     * 图标名称
     */
    private String iconName;

    /**
     * 权益值(如"95折","3天")
     */
    private String benefitValue;

    /**
     * 标签类型(success/warning/danger/info)
     */
    private String tagType;

    /**
     * 适用等级(1、2、3)
     */
    private String levelValue;

    /**
     * 排序
     */
    private Long sortOrder;

    /**
     * 状态(0正常 1停用)
     */
    private String status;

    /**
     * 备注
     */
    private String remark;


}
