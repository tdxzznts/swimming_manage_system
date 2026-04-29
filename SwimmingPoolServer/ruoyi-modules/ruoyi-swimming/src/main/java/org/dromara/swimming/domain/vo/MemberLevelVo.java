package org.dromara.swimming.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 会员等级返回对象
 *
 * @author W
 * @date 2026-02-04
 */
@Data
public class MemberLevelVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 等级ID
     */
    private Long id;

    /**
     * 等级类型
     */
    private String type;

    /**
     * 等级名称
     */
    private String name;

    /**
     * 等级描述
     */
    private String desc;

    /**
     * 年费价格
     */
    private Double price;

    /**
     * 英文名称
     */
    private String levelEn;
}
