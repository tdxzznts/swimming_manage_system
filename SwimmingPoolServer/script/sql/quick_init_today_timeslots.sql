-- ===================================================================
-- 快速初始化 - 只创建今天的时段数据
-- 适合快速测试使用
-- ===================================================================

-- 1. 先修改表结构支持自增（推荐方式）
-- --------------------
ALTER TABLE sp_lane MODIFY COLUMN id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID';
ALTER TABLE sp_time_slot MODIFY COLUMN id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID';

-- 2. 清理旧数据
-- --------------------
DELETE FROM sp_time_slot WHERE DATE(slot_date) >= CURDATE();
DELETE FROM sp_lane;

-- 3. 重置自增值
-- --------------------
ALTER TABLE sp_lane AUTO_INCREMENT = 1;
ALTER TABLE sp_time_slot AUTO_INCREMENT = 1;

-- 4. 插入泳道数据（不指定id，让数据库自动生成）
-- --------------------
INSERT INTO sp_lane (lane_no, lane_type, status, capacity, length, width, depth, sort, create_time, update_time) VALUES
('1号泳道', '0', '0', 10, 50, 5, 1.8, 1, NOW(), NOW()),
('2号泳道', '1', '0', 8, 50, 5, 1.5, 2, NOW(), NOW()),
('3号泳道', '2', '0', 6, 50, 5, 1.2, 3, NOW(), NOW());

-- 5. 插入今天的时段数据（不指定id，让数据库自动生成）
-- --------------------
INSERT INTO sp_time_slot (lane_id, slot_date, start_time, end_time, slot_type, is_available, capacity, booked_count, price, create_time, update_time)
SELECT
    lane_id,
    CURDATE() as slot_date,
    start_time,
    end_time,
    slot_type,
    '1' as is_available,
    CASE lane_id
        WHEN 1 THEN 10  -- 1号泳道容量10
        WHEN 2 THEN 8   -- 2号泳道容量8
        WHEN 3 THEN 6   -- 3号泳道容量6
    END as capacity,
    0 as booked_count,
    CASE
        WHEN slot_type = '1' THEN 5000  -- 高峰期 50元
        ELSE 3000  -- 低峰期 30元
    END as price,
    NOW() as create_time,
    NOW() as update_time
FROM (
    -- 泳道1的时段
    SELECT 1 as lane_id, '08:00:00' as start_time, '10:00:00' as end_time, '1' as slot_type
    UNION ALL SELECT 1, '10:00:00', '12:00:00', '1'
    UNION ALL SELECT 1, '14:00:00', '16:00:00', '1'
    UNION ALL SELECT 1, '16:00:00', '18:00:00', '2'

    -- 泳道2的时段
    UNION ALL SELECT 2, '08:00:00', '10:00:00', '1'
    UNION ALL SELECT 2, '10:00:00', '12:00:00', '1'
    UNION ALL SELECT 2, '14:00:00', '16:00:00', '1'
    UNION ALL SELECT 2, '16:00:00', '18:00:00', '2'

    -- 泳道3的时段
    UNION ALL SELECT 3, '08:00:00', '10:00:00', '1'
    UNION ALL SELECT 3, '10:00:00', '12:00:00', '1'
    UNION ALL SELECT 3, '14:00:00', '16:00:00', '1'
    UNION ALL SELECT 3, '16:00:00', '18:00:00', '2'
) t;

-- 6. 验证数据
-- --------------------
-- 查看泳道
SELECT '泳道数据：' as info;
SELECT id, lane_no, lane_type, capacity FROM sp_lane ORDER BY sort;

-- 查看今天的时段
SELECT '今日时段数据：' as info;
SELECT
    ts.id,
    l.lane_no,
    DATE_FORMAT(ts.start_time, '%H:%i') as start_time,
    DATE_FORMAT(ts.end_time, '%H:%i') as end_time,
    CASE ts.slot_type
        WHEN '1' THEN '高峰期'
        WHEN '2' THEN '低峰期'
        ELSE '未知'
    END as slot_type_name,
    ts.capacity,
    ts.booked_count,
    (ts.capacity - ts.booked_count) as remaining_count,
    ts.price / 100 as price_yuan
FROM sp_time_slot ts
LEFT JOIN sp_lane l ON ts.lane_id = l.id
WHERE DATE(ts.slot_date) = CURDATE()
    AND ts.is_available = '1'
ORDER BY ts.start_time, l.sort;

-- ===================================================================
-- 如果ALTER TABLE权限不足，可以使用下面的手动指定ID版本
-- ===================================================================

-- 清理数据
-- DELETE FROM sp_time_slot WHERE DATE(slot_date) >= CURDATE();
-- DELETE FROM sp_lane WHERE id IN (1, 2, 3);

-- 手动指定ID插入泳道
-- INSERT INTO sp_lane (id, lane_no, lane_type, status, capacity, length, width, depth, sort, create_time, update_time) VALUES
-- (1, '1号泳道', '0', '0', 10, 50, 5, 1.8, 1, NOW(), NOW()),
-- (2, '2号泳道', '1', '0', 8, 50, 5, 1.5, 2, NOW(), NOW()),
-- (3, '3号泳道', '2', '0', 6, 50, 5, 1.2, 3, NOW(), NOW());

-- 手动指定ID插入时段（今天12个时段，ID从1开始）
-- INSERT INTO sp_time_slot (id, lane_id, slot_date, start_time, end_time, slot_type, is_available, capacity, booked_count, price, create_time, update_time) VALUES
-- (1, 1, CURDATE(), '08:00:00', '10:00:00', '1', '1', 10, 0, 5000, NOW(), NOW()),
-- (2, 1, CURDATE(), '10:00:00', '12:00:00', '1', '1', 10, 0, 5000, NOW(), NOW()),
-- (3, 1, CURDATE(), '14:00:00', '16:00:00', '1', '1', 10, 0, 5000, NOW(), NOW()),
-- (4, 1, CURDATE(), '16:00:00', '18:00:00', '0', '1', 10, 0, 3000, NOW(), NOW()),
-- (5, 2, CURDATE(), '08:00:00', '10:00:00', '1', '1', 8, 0, 5000, NOW(), NOW()),
-- (6, 2, CURDATE(), '10:00:00', '12:00:00', '1', '1', 8, 0, 5000, NOW(), NOW()),
-- (7, 2, CURDATE(), '14:00:00', '16:00:00', '1', '1', 8, 0, 5000, NOW(), NOW()),
-- (8, 2, CURDATE(), '16:00:00', '18:00:00', '0', '1', 8, 0, 3000, NOW(), NOW()),
-- (9, 3, CURDATE(), '08:00:00', '10:00:00', '1', '1', 6, 0, 5000, NOW(), NOW()),
-- (10, 3, CURDATE(), '10:00:00', '12:00:00', '1', '1', 6, 0, 5000, NOW(), NOW()),
-- (11, 3, CURDATE(), '14:00:00', '16:00:00', '1', '1', 6, 0, 5000, NOW(), NOW()),
-- (12, 3, CURDATE(), '16:00:00', '18:00:00', '0', '1', 6, 0, 3000, NOW(), NOW());

-- ===================================================================
-- 使用说明：
-- 方法1（推荐）：直接执行整个脚本，会自动修改表结构并插入数据
-- 方法2：如果没有ALTER权限，使用注释部分的手动指定ID版本
-- ===================================================================
