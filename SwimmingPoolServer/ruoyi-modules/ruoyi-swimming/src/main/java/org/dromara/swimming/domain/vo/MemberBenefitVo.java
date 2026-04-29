package org.dromara.swimming.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 会员权益返回对象
 *
 * @author W
 * @date 2026-02-04
 */
@Data
public class MemberBenefitVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 权益ID
     */
    private Long id;

    /**
     * 权益编码
     */
    private String benefitCode;

    /**
     * 适用等级(1银卡 2金卡 3钻石)
     */
    private Integer levelValue;

    /**
     * 图标名称
     */
    private String icon;

    /**
     * 权益名称
     */
    private String name;

    /**
     * 权益描述
     */
    private String desc;

    /**
     * 权益值
     */
    private String value;

    /**
     * 标签类型
     */
    private String tagType;
}
