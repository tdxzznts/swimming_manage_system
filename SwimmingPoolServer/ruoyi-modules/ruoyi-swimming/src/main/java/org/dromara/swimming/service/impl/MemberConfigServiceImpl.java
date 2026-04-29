package org.dromara.swimming.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.swimming.domain.SpMemberLevel;
import org.dromara.swimming.domain.SpMemberCard;
import org.dromara.swimming.domain.SpMemberBenefit;
import org.dromara.swimming.domain.vo.MemberBenefitVo;
import org.dromara.swimming.domain.vo.MemberLevelVo;
import org.dromara.swimming.domain.vo.MemberInfoVo;
import org.dromara.swimming.domain.vo.MemberRulesVo;
import org.dromara.swimming.mapper.SpMemberLevelMapper;
import org.dromara.swimming.mapper.SpMemberCardMapper;
import org.dromara.swimming.mapper.SpMemberBenefitMapper;
import org.dromara.swimming.service.IMemberConfigService;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 会员配置Service实现
 *
 * @author W
 * @date 2026-02-04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MemberConfigServiceImpl implements IMemberConfigService {

    private final SpMemberLevelMapper memberLevelMapper;
    private final SpMemberBenefitMapper memberBenefitMapper;
    private final SpMemberCardMapper memberCardMapper;

    @Override
    public List<MemberLevelVo> getMemberLevels() {
        LambdaQueryWrapper<SpMemberLevel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SpMemberLevel::getStatus, "0")
               .orderByAsc(SpMemberLevel::getSortOrder);

        List<SpMemberLevel> levels = memberLevelMapper.selectList(wrapper);
        return levels.stream().map(level -> {
            MemberLevelVo vo = new MemberLevelVo();
            vo.setId(level.getId());
            vo.setType(level.getLevelCode());
            vo.setName(level.getLevelName());
            vo.setDesc(level.getDescription());
            vo.setPrice(level.getPrice() != null ? level.getPrice().doubleValue() : 0.0);
            vo.setLevelEn(level.getLevelEn());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, String>> getMemberLevelDict() {
        LambdaQueryWrapper<SpMemberLevel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SpMemberLevel::getStatus, "0")
               .orderByAsc(SpMemberLevel::getSortOrder);

        List<SpMemberLevel> levels = memberLevelMapper.selectList(wrapper);
        return levels.stream().map(level -> {
            Map<String, String> map = new java.util.HashMap<>();
            map.put("value", level.getLevelValue().toString());  // 字典值：1/2/3/4
            map.put("label", level.getLevelName());             // 字典标签：普通/银卡/金卡/钻石
            map.put("type", "default");                          // 标签类型
            map.put("cssClass", "");                             // CSS类名
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<MemberBenefitVo> getMemberBenefits(String levelCode) {
        // 将字符串等级转换为数字
        Integer levelValue = Integer.parseInt(levelCode);

        LambdaQueryWrapper<SpMemberBenefit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SpMemberBenefit::getStatus, "0")
               .orderByAsc(SpMemberBenefit::getSortOrder);

        List<SpMemberBenefit> allBenefits = memberBenefitMapper.selectList(wrapper);

        // 过滤出适用当前等级的权益
        return allBenefits.stream()
            .filter(benefit -> isBenefitForLevel(benefit, levelValue))
            .map(benefit -> {
                MemberBenefitVo vo = new MemberBenefitVo();
                vo.setId(benefit.getId());
                vo.setBenefitCode(benefit.getBenefitCode());
                vo.setIcon(benefit.getIconName());
                vo.setName(benefit.getBenefitName());
                vo.setDesc(benefit.getBenefitDesc());
                vo.setValue(benefit.getBenefitValue());
                vo.setTagType(benefit.getTagType());
                return vo;
            })
            .collect(Collectors.toList());
    }

    private boolean isBenefitForLevel(SpMemberBenefit benefit, Integer levelValue) {
        Integer benefitLevelValue = Integer.valueOf(benefit.getLevelValue());
        if (benefitLevelValue == null) {
            return true; // 如果为空，表示所有等级都适用
        }
        return benefitLevelValue.equals(levelValue);
    }

    @Override
    public MemberInfoVo getMemberInfo() {
        Long userId = LoginHelper.getUserId();
        log.info("查询用户会员信息, userId: {}", userId);

        LambdaQueryWrapper<SpMemberCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SpMemberCard::getUserId, userId);
        SpMemberCard memberCard = memberCardMapper.selectOne(wrapper);

        log.info("会员卡查询结果: {}", memberCard);

        MemberInfoVo vo = new MemberInfoVo();
        if (memberCard != null) {
            // 根据cardLevel查询等级信息
            SpMemberLevel level = memberLevelMapper.selectOne(
                new LambdaQueryWrapper<SpMemberLevel>()
                    .eq(SpMemberLevel::getLevelValue, Integer.valueOf(memberCard.getCardLevel()))
            );

            vo.setLevel(Integer.valueOf(memberCard.getCardLevel()));
            vo.setLevelName(level != null ? level.getLevelName() : "普通会员");
            vo.setLevelEn(level != null ? level.getLevelEn() : "NORMAL");
            vo.setCardNo(memberCard.getCardNo());
            vo.setBalance(memberCard.getBalance() != null ? memberCard.getBalance() : 0L);
            vo.setPoints(0); // 如果有积分字段，从memberCard获取

            if (memberCard.getExpiryDate() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                vo.setValidThru(sdf.format(memberCard.getExpiryDate()));
            }

            // 获取用户名
            vo.setUserName(LoginHelper.getUsername());

            // 查询优先预约天数权益
            SpMemberBenefit priorityBenefit = memberBenefitMapper.selectOne(
                new LambdaQueryWrapper<SpMemberBenefit>()
                    .eq(SpMemberBenefit::getStatus, "0")
                    .eq(SpMemberBenefit::getBenefitCode, "PRIORITY_BOOKING")
                    .eq(SpMemberBenefit::getLevelValue, Integer.valueOf(memberCard.getCardLevel()))
            );

            if (priorityBenefit != null && priorityBenefit.getBenefitValue() != null) {
                try {
                    vo.setPriorityBookingDays(Integer.parseInt(priorityBenefit.getBenefitValue()));
                } catch (NumberFormatException e) {
                    vo.setPriorityBookingDays(0);
                }
            } else {
                vo.setPriorityBookingDays(0);
            }
        } else {
            // 用户没有会员卡，返回默认值
            log.info("用户没有会员卡，返回默认信息");
            vo.setLevel(1);
            vo.setLevelName("普通会员");
            vo.setLevelEn("NORMAL");
            vo.setCardNo("未开通");
            vo.setBalance(0L);
            vo.setPoints(0);
            vo.setValidThru("");
            vo.setUserName(LoginHelper.getUsername());
            vo.setPriorityBookingDays(0);
        }

        return vo;
    }

    @Override
    public MemberRulesVo getMemberRules() {
        MemberRulesVo vo = new MemberRulesVo();

        // 获取所有等级
        vo.setLevels(getMemberLevels());

        // 获取所有权益（不限等级）
        LambdaQueryWrapper<SpMemberBenefit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SpMemberBenefit::getStatus, "0")
               .orderByAsc(SpMemberBenefit::getSortOrder);

        List<SpMemberBenefit> allBenefits = memberBenefitMapper.selectList(wrapper);
        List<MemberBenefitVo> benefits = allBenefits.stream()
            .map(benefit -> {
                MemberBenefitVo benefitVo = new MemberBenefitVo();
                benefitVo.setId(benefit.getId());
                benefitVo.setBenefitCode(benefit.getBenefitCode());
                benefitVo.setLevelValue(Integer.valueOf(benefit.getLevelValue()));
                benefitVo.setIcon(benefit.getIconName());
                benefitVo.setName(benefit.getBenefitName());
                benefitVo.setDesc(benefit.getBenefitDesc());
                benefitVo.setValue(benefit.getBenefitValue());
                benefitVo.setTagType(benefit.getTagType());
                return benefitVo;
            })
            .collect(Collectors.toList());
        vo.setBenefits(benefits);

        // 设置规则说明
        vo.setRuleDescription("1. 会员等级根据累计充值+消费金额自动升级\n" +
                             "2. 累计金额达到对应等级要求后自动升级\n" +
                             "3. 会员权益享受等级越高，优惠越多\n" +
                             "4. 充值金额实时到账，可随时消费");

        return vo;
    }
}
