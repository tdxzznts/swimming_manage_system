-- 插入会员权益测试数据
-- 说明：为不同等级的会员添加预约折扣、积分倍数、免费次数等权益

-- 清理现有权益数据（可选）
-- DELETE FROM sp_member_benefit WHERE benefit_code IN ('BOOKING_DISCOUNT', 'POINTS_DOUBLE', 'FREE_TIMES');

-- 为普通会员(card_level=0)添加权益
INSERT INTO sp_member_benefit (benefit_code, benefit_name, benefit_value, benefit_desc, level_value, icon_name, tag_type, sort_order, status, create_time, update_time)
VALUES
('BOOKING_DISCOUNT', '预约折扣', '100', '普通会员享受原价预约', 0, 'discount', 'info', 1, '0', NOW(), NOW()),
('POINTS_DOUBLE', '积分倍数', '1', '消费金额1倍积分', 0, 'point', 'info', 2, '0', NOW(), NOW()),
('FREE_TIMES', '免费次数', '0', '暂无免费次数', 0, 'free', 'info', 3, '0', NOW(), NOW())
ON DUPLICATE KEY UPDATE
benefit_name = VALUES(benefit_name),
benefit_value = VALUES(benefit_value),
benefit_desc = VALUES(benefit_desc),
update_time = NOW();

-- 为银卡会员(card_level=1)添加权益
INSERT INTO sp_member_benefit (benefit_code, benefit_name, benefit_value, benefit_desc, level_value, icon_name, tag_type, sort_order, status, create_time, update_time)
VALUES
('BOOKING_DISCOUNT', '预约折扣', '95', '银卡会员享受95折优惠', 1, 'discount', 'warning', 1, '0', NOW(), NOW()),
('POINTS_DOUBLE', '积分倍数', '2', '消费金额2倍积分', 1, 'point', 'success', 2, '0', NOW(), NOW()),
('FREE_TIMES', '免费次数', '5', '每月5次免费预约', 1, 'free', 'primary', 3, '0', NOW(), NOW())
ON DUPLICATE KEY UPDATE
benefit_name = VALUES(benefit_name),
benefit_value = VALUES(benefit_value),
benefit_desc = VALUES(benefit_desc),
update_time = NOW();

-- 为金卡会员(card_level=2)添加权益
INSERT INTO sp_member_benefit (benefit_code, benefit_name, benefit_value, benefit_desc, level_value, icon_name, tag_type, sort_order, status, create_time, update_time)
VALUES
('BOOKING_DISCOUNT', '预约折扣', '90', '金卡会员享受90折优惠', 2, 'discount', 'warning', 1, '0', NOW(), NOW()),
('POINTS_DOUBLE', '积分倍数', '3', '消费金额3倍积分', 2, 'point', 'success', 2, '0', NOW(), NOW()),
('FREE_TIMES', '免费次数', '10', '每月10次免费预约', 2, 'free', 'primary', 3, '0', NOW(), NOW())
ON DUPLICATE KEY UPDATE
benefit_name = VALUES(benefit_name),
benefit_value = VALUES(benefit_value),
benefit_desc = VALUES(benefit_desc),
update_time = NOW();

-- 为钻石会员(card_level=3)添加权益
INSERT INTO sp_member_benefit (benefit_code, benefit_name, benefit_value, benefit_desc, level_value, icon_name, tag_type, sort_order, status, create_time, update_time)
VALUES
('BOOKING_DISCOUNT', '预约折扣', '85', '钻石会员享受85折优惠', 3, 'discount', 'danger', 1, '0', NOW(), NOW()),
('POINTS_DOUBLE', '积分倍数', '5', '消费金额5倍积分', 3, 'point', 'success', 2, '0', NOW(), NOW()),
('FREE_TIMES', '免费次数', '20', '每月20次免费预约', 3, 'free', 'primary', 3, '0', NOW(), NOW())
ON DUPLICATE KEY UPDATE
benefit_name = VALUES(benefit_name),
benefit_value = VALUES(benefit_value),
benefit_desc = VALUES(benefit_desc),
update_time = NOW();

-- 更新会员卡的免费次数（给测试用户添加一些免费次数）
-- 注意：需要根据实际情况修改user_id
UPDATE sp_member_card
SET free_times = 5
WHERE card_level = '1' AND free_times IS NULL;

UPDATE sp_member_card
SET free_times = 10
WHERE card_level = '2' AND free_times IS NULL;

UPDATE sp_member_card
SET free_times = 20
WHERE card_level = '3' AND free_times IS NULL;
