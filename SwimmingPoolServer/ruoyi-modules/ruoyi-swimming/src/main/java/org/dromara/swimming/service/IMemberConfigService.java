package org.dromara.swimming.service;

import org.dromara.swimming.domain.vo.MemberBenefitVo;
import org.dromara.swimming.domain.vo.MemberLevelVo;
import org.dromara.swimming.domain.vo.MemberInfoVo;
import org.dromara.swimming.domain.vo.MemberRulesVo;

import java.util.List;
import java.util.Map;

/**
 * 会员配置Service接口
 *
 * @author W
 * @date 2026-02-04
 */
public interface IMemberConfigService {

    /**
     * 获取所有会员等级
     *
     * @return 会员等级列表
     */
    List<MemberLevelVo> getMemberLevels();

    /**
     * 获取会员等级字典数据（用于前端下拉框）
     *
     * @return 字典数据列表
     */
    List<Map<String, String>> getMemberLevelDict();

    /**
     * 获取指定等级的会员权益
     *
     * @param levelCode 等级编码
     * @return 会员权益列表
     */
    List<MemberBenefitVo> getMemberBenefits(String levelCode);

    /**
     * 获取当前用户会员信息
     *
     * @return 会员信息
     */
    MemberInfoVo getMemberInfo();

    /**
     * 获取会员规则（包含所有等级和权益）
     *
     * @return 会员规则信息
     */
    MemberRulesVo getMemberRules();
}
