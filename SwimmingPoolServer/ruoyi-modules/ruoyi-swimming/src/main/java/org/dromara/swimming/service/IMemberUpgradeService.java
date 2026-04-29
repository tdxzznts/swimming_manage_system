package org.dromara.swimming.service;

/**
 * 会员升级服务接口
 *
 * @author W
 * @date 2026-02-04
 */
public interface IMemberUpgradeService {

    /**
     * 检查并升级会员等级
     * 根据用户的累计充值和累计消费金额，自动升级会员等级
     *
     * @param userId 用户ID
     * @return 是否升级成功（false表示无需升级或升级失败）
     */
    Boolean checkAndUpgradeMemberLevel(Long userId);

    /**
     * 升级会员等级到指定等级
     *
     * @param userId 用户ID
     * @param targetLevel 目标等级值
     * @return 是否升级成功
     */
    Boolean upgradeMemberLevel(Long userId, Long targetLevel);
}
