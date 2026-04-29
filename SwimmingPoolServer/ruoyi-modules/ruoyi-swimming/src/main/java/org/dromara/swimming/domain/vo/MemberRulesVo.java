package org.dromara.swimming.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * 会员规则VO
 *
 * @author W
 * @date 2026-02-04
 */
@Data
public class MemberRulesVo {

    /**
     * 会员等级列表
     */
    private List<MemberLevelVo> levels;

    /**
     * 会员权益列表
     */
    private List<MemberBenefitVo> benefits;

    /**
     * 规则说明
     */
    private String ruleDescription;
}
