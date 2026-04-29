package org.dromara.swimming.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 会员权益配置对象 sp_member_benefit
 *
 * @author W
 * @date 2026-02-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sp_member_benefit")
public class SpMemberBenefit extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id")
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
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;

    /**
     * 备注
     */
    private String remark;


}
