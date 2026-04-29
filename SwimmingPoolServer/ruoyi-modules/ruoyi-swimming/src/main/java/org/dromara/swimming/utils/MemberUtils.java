package org.dromara.swimming.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.swimming.service.IMemberUpgradeService;
import org.springframework.stereotype.Component;

/**
 * 会员工具类
 *
 * @author W
 * @date 2026-02-04
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MemberUtils {

    private final IMemberUpgradeService memberUpgradeService;

    /**
     * 检查并升级会员等级
     * 用于在充值、消费等操作后自动检查是否满足升级条件
     *
     * @param userId 用户ID
     */
    public void checkAndUpgrade(Long userId) {
        try {
            Boolean upgraded = memberUpgradeService.checkAndUpgradeMemberLevel(userId);
            if (upgraded) {
                log.info("会员等级已自动升级，userId: {}", userId);
            }
        } catch (Exception e) {
            log.error("会员等级升级失败，userId: {}", userId, e);
        }
    }

    /**
     * 手动升级会员到指定等级
     *
     * @param userId 用户ID
     * @param targetLevel 目标等级值（1-普通 2-银卡 3-金卡 4-钻石）
     * @return 是否升级成功
     */
    public Boolean upgradeToLevel(Long userId, Long targetLevel) {
        return memberUpgradeService.upgradeMemberLevel(userId, targetLevel);
    }
}
