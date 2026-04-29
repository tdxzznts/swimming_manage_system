-- ----------------------------
-- 会员等级配置表
-- ----------------------------
DROP TABLE IF EXISTS `sp_member_level`;
CREATE TABLE `sp_member_level` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `level_code` VARCHAR(20) NOT NULL COMMENT '等级编码(normal/silver/gold/diamond)',
  `level_name` VARCHAR(50) NOT NULL COMMENT '等级名称',
  `level_en` VARCHAR(50) COMMENT '英文名称',
  `level_value` INT NOT NULL COMMENT '等级值(1-4)',
  `discount` DECIMAL(5,2) COMMENT '折扣比例(如95表示95折)',
  `price` DECIMAL(10,2) COMMENT '年费价格',
  `card_color_start` VARCHAR(20) COMMENT '卡片渐变起始色',
  `card_color_end` VARCHAR(20) COMMENT '卡片渐变结束色',
  `description` VARCHAR(200) COMMENT '等级描述',
  `status` CHAR(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `create_dept` BIGINT(20) COMMENT '创建部门',
  `create_by` BIGINT(20) COMMENT '创建者',
  `create_time` DATETIME COMMENT '创建时间',
  `update_by` BIGINT(20) COMMENT '更新者',
  `update_time` DATETIME COMMENT '更新时间',
  `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `remark` VARCHAR(500) COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_level_code` (`level_code`),
  KEY `idx_level_value` (`level_value`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='会员等级配置表';

-- ----------------------------
-- 会员权益配置表
-- ----------------------------
DROP TABLE IF EXISTS `sp_member_benefit`;
CREATE TABLE `sp_member_benefit` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `benefit_code` VARCHAR(50) NOT NULL COMMENT '权益编码',
  `benefit_name` VARCHAR(50) NOT NULL COMMENT '权益名称',
  `benefit_desc` VARCHAR(100) COMMENT '权益描述',
  `icon_name` VARCHAR(50) COMMENT '图标名称',
  `benefit_value` VARCHAR(50) COMMENT '权益值(如"95折","3天")',
  `tag_type` VARCHAR(20) COMMENT '标签类型(success/warning/danger/info)',
  `level_codes` VARCHAR(200) COMMENT '适用等级(逗号分隔,如"silver,gold,diamond")',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` CHAR(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_dept` BIGINT(20) COMMENT '创建部门',
  `create_by` BIGINT(20) COMMENT '创建者',
  `create_time` DATETIME COMMENT '创建时间',
  `update_by` BIGINT(20) COMMENT '更新者',
  `update_time` DATETIME COMMENT '更新时间',
  `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `remark` VARCHAR(500) COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_benefit_code` (`benefit_code`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='会员权益配置表';

-- ----------------------------
-- 初始化会员等级数据
-- ----------------------------
INSERT INTO `sp_member_level` (`id`, `level_code`, `level_name`, `level_en`, `level_value`, `discount`, `price`, `card_color_start`, `card_color_end`, `description`, `status`, `sort_order`, `create_dept`, `create_by`, `create_time`, `del_flag`) VALUES
(1, 'normal', '普通会员', 'NORMAL', 1, 100.00, 0, '#6B7280', '#9CA3AF', '基础会员权益', '0', 1, NULL, 1, NOW(), '0'),
(2, 'silver', '银卡会员', 'SILVER', 2, 95.00, 199, '#64748B', '#94A3B8', '享受95折优惠', '0', 2, NULL, 1, NOW(), '0'),
(3, 'gold', '金卡会员', 'GOLD', 3, 90.00, 499, '#B45309', '#F59E0B', '享受9折优惠', '0', 3, NULL, 1, NOW(), '0'),
(4, 'diamond', '钻石会员', 'DIAMOND', 4, 85.00, 999, '#9333EA', '#A855F7', '享受85折优惠', '0', 4, NULL, 1, NOW(), '0');

-- ----------------------------
-- 初始化会员权益数据
-- ----------------------------
INSERT INTO `sp_member_benefit` (`id`, `benefit_code`, `benefit_name`, `benefit_desc`, `icon_name`, `benefit_value`, `tag_type`, `level_codes`, `sort_order`, `status`, `create_dept`, `create_by`, `create_time`, `del_flag`) VALUES
(1, 'BOOKING_DISCOUNT', '预约折扣', '会员享受专属折扣', 'Discount', '95折', 'success', 'silver,gold,diamond', 1, '0', NULL, 1, NOW(), '0'),
(2, 'PRIORITY_BOOKING', '优先预约', '提前预约时段', 'Calendar', '3天', 'warning', 'silver,gold,diamond', 2, '0', NULL, 1, NOW(), '0'),
(3, 'FREE_TIMES', '免费次数', '每月免费游泳次数', 'Tickets', '2次', 'danger', 'silver,gold,diamond', 3, '0', NULL, 1, NOW(), '0'),
(4, 'EXTEND_TIME', '延长时长', '单次可延长时长', 'Timer', '+30分钟', 'info', 'gold,diamond', 4, '0', NULL, 1, NOW(), '0'),
(5, 'VIP_SERVICE', '专属客服', '7×24小时在线', 'Headset', 'VIP', 'success', 'gold,diamond', 5, '0', NULL, 1, NOW(), '0'),
(6, 'POINTS_DOUBLE', '积分翻倍', '消费积分倍数', 'Coin', '2倍', 'warning', 'diamond', 6, '0', NULL, 1, NOW(), '0');
