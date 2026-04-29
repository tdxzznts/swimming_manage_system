package org.dromara.swimming.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.swimming.domain.SpMemberBenefit;
import org.dromara.swimming.domain.SpMemberCard;
import org.dromara.swimming.domain.SpMemberLevel;
import org.dromara.swimming.mapper.SpMemberBenefitMapper;
import org.dromara.swimming.mapper.SpMemberCardMapper;
import org.dromara.swimming.mapper.SpMemberLevelMapper;
import org.dromara.swimming.service.IMemberUpgradeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 会员升级服务实现
 *
 * @author W
 * @date 2026-02-04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MemberUpgradeServiceImpl implements IMemberUpgradeService {

    private final SpMemberCardMapper memberCardMapper;
    private final SpMemberLevelMapper memberLevelMapper;
    private final SpMemberBenefitMapper memberBenefitMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean checkAndUpgradeMemberLevel(Long userId) {
        // 查询用户会员卡
        LambdaQueryWrapper<SpMemberCard> cardWrapper = new LambdaQueryWrapper<>();
        cardWrapper.eq(SpMemberCard::getUserId, userId);
        SpMemberCard memberCard = memberCardMapper.selectOne(cardWrapper);

        if (memberCard == null) {
            log.warn("用户会员卡不存在，userId: {}", userId);
            return false;
        }

        // 获取当前等级
        Integer currentLevel = Integer.valueOf(memberCard.getCardLevel());
        log.info("检查会员升级，userId: {}, 当前等级: {}", userId, currentLevel);

        // 查询所有会员等级配置（按price升序）
        LambdaQueryWrapper<SpMemberLevel> levelWrapper = new LambdaQueryWrapper<>();
        levelWrapper.eq(SpMemberLevel::getStatus, "0")
                   .orderByAsc(SpMemberLevel::getPrice);
        List<SpMemberLevel> levels = memberLevelMapper.selectList(levelWrapper);

        if (levels == null || levels.isEmpty()) {
            log.warn("未找到会员等级配置");
            return false;
        }

        // 计算累计充值 + 累计消费（单位：元）
        BigDecimal totalRecharge = memberCard.getTotalRecharge() != null ?
            new BigDecimal(memberCard.getTotalRecharge()) : BigDecimal.ZERO;
        BigDecimal totalConsume = memberCard.getTotalConsume() != null ?
            new BigDecimal(memberCard.getTotalConsume()) : BigDecimal.ZERO;
        BigDecimal totalAmount = totalRecharge.add(totalConsume);

        log.info("用户累计金额，userId: {}, 累计充值: {}元, 累计消费: {}元, 总计: {}元",
            userId, totalRecharge, totalConsume, totalAmount);

        // 查找满足条件的最高等级
        SpMemberLevel targetLevel = null;
        for (SpMemberLevel level : levels) {
            BigDecimal price = level.getPrice() != null ? BigDecimal.valueOf(level.getPrice()) : BigDecimal.ZERO;

            if (totalAmount.compareTo(price) >= 0 && level.getLevelValue() > currentLevel) {
                targetLevel = level;
            }
        }

        // 如果找到满足条件的更高等级，进行升级
        if (targetLevel != null) {
            log.info("用户满足升级条件，userId: {}, 当前等级: {}, 目标等级: {}, 目标等级价格: {}元",
                userId, currentLevel, targetLevel.getLevelValue(), targetLevel.getPrice());

            return upgradeMemberLevel(userId, targetLevel.getLevelValue());
        }

        log.info("用户无需升级，userId: {}, 当前等级: {}", userId, currentLevel);
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean upgradeMemberLevel(Long userId, Long targetLevel) {
        // 查询用户会员卡
        LambdaQueryWrapper<SpMemberCard> cardWrapper = new LambdaQueryWrapper<>();
        cardWrapper.eq(SpMemberCard::getUserId, userId);
        SpMemberCard memberCard = memberCardMapper.selectOne(cardWrapper);

        if (memberCard == null) {
            log.error("用户会员卡不存在，userId: {}", userId);
            return false;
        }

        // 查询目标等级配置
        LambdaQueryWrapper<SpMemberLevel> levelWrapper = new LambdaQueryWrapper<>();
        levelWrapper.eq(SpMemberLevel::getLevelValue, targetLevel)
                   .eq(SpMemberLevel::getStatus, "0");
        SpMemberLevel levelConfig = memberLevelMapper.selectOne(levelWrapper);

        if (levelConfig == null) {
            log.error("目标等级配置不存在，targetLevel: {}", targetLevel);
            return false;
        }

        // 更新会员卡等级
        Integer oldLevel = Integer.valueOf(memberCard.getCardLevel());
        memberCard.setCardLevel(targetLevel.toString());
        //填充会员权益的免费次数
        LambdaQueryWrapper<SpMemberBenefit> benefitWrapper = new LambdaQueryWrapper<>();
        benefitWrapper.eq(SpMemberBenefit::getLevelValue, targetLevel);
        benefitWrapper.eq(SpMemberBenefit::getBenefitCode,"FREE_TIMES");
        SpMemberBenefit spMemberBenefit = memberBenefitMapper.selectOne(benefitWrapper);

        memberCard.setFreeTimes(Long.valueOf(spMemberBenefit.getBenefitValue()));
        int rows = memberCardMapper.updateById(memberCard);

        if (rows > 0) {
            log.info("会员等级升级成功，userId: {}, 原等级: {}, 新等级: {}, 等级名称: {}",
                userId, oldLevel, targetLevel, levelConfig.getLevelName());
            return true;
        } else {
            log.error("会员等级升级失败，userId: {}", userId);
            return false;
        }
    }
}
