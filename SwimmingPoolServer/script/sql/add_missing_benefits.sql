-- 添加缺失的权益数据

-- 为钻石会员(level_value=3)添加积分倍数权益
INSERT INTO `swimming_pool`.`sp_member_benefit`
(`benefit_code`, `benefit_name`, `benefit_desc`, `icon_name`, `benefit_value`, `tag_type`, `level_value`, `sort_order`, `status`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `remark`)
VALUES
('POINTS_DOUBLE', '积分翻倍', '消费积分倍数', 'Coin', '5', 'warning', '3', 6, '0', NULL, 1, NOW(), NULL, NULL, '0', NULL);

-- 修复金卡会员的预约折扣值（从9改为90）
UPDATE `swimming_pool`.`sp_member_benefit`
SET benefit_value = '90', update_time = NOW()
WHERE benefit_code = 'BOOKING_DISCOUNT' AND level_value = '2';

-- 验证数据
SELECT
    benefit_code,
    benefit_name,
    benefit_value,
    level_value
FROM sp_member_benefit
WHERE benefit_code IN ('BOOKING_DISCOUNT', 'POINTS_DOUBLE', 'FREE_TIMES')
ORDER BY level_value, benefit_code;
