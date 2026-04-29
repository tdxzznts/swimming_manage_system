-- ===================================================================
-- 智能游泳馆 - 完整测试数据初始化脚本
-- 包含：泳道、时段配置
-- ===================================================================

-- 1. 检查并插入泳道数据
-- --------------------
DELETE FROM sp_lane WHERE id IN (1, 2, 3);

INSERT INTO sp_lane (id, lane_no, lane_type, status, capacity, length, width, depth, sort, create_time, update_time) VALUES
(1, '1号泳道', '0', '0', 10, 50, 5, 1.8, 1, NOW(), NOW()),
(2, '2号泳道', '1', '0', 8, 50, 5, 1.5, 2, NOW(), NOW()),
(3, '3号泳道', '2', '0', 6, 50, 5, 1.2, 3, NOW(), NOW());

-- 验证泳道数据
SELECT * FROM sp_lane ORDER BY sort;

-- 2. 清理并插入时段数据
-- --------------------
DELETE FROM sp_time_slot WHERE DATE(slot_date) >= CURDATE();

-- 设置变量
SET @today = CURDATE();

-- 为3条泳道创建未来7天的时段
-- 每天每条泳道5个时段：
--   06:00-08:00 低峰期 (30元)
--   08:00-10:00 高峰期 (50元)
--   10:00-12:00 高峰期 (50元)
--   14:00-16:00 高峰期 (50元)
--   16:00-18:00 低峰期 (30元)
INSERT INTO sp_time_slot (lane_id, slot_date, start_time, end_time, slot_type, is_available, capacity, booked_count, price, create_time, update_time)
SELECT
    lane_id,
    DATE_ADD(@today, INTERVAL days DAY) as slot_date,
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
    -- 泳道1时段
    SELECT 1 as lane_id, '06:00:00' as start_time, '08:00:00' as end_time, '0' as slot_type
    UNION ALL SELECT 1, '08:00:00', '10:00:00', '1'
    UNION ALL SELECT 1, '10:00:00', '12:00:00', '1'
    UNION ALL SELECT 1, '14:00:00', '16:00:00', '1'
    UNION ALL SELECT 1, '16:00:00', '18:00:00', '0'

    -- 泳道2时段
    UNION ALL SELECT 2, '06:00:00', '08:00:00', '0'
    UNION ALL SELECT 2, '08:00:00', '10:00:00', '1'
    UNION ALL SELECT 2, '10:00:00', '12:00:00', '1'
    UNION ALL SELECT 2, '14:00:00', '16:00:00', '1'
    UNION ALL SELECT 2, '16:00:00', '18:00:00', '0'

    -- 泳道3时段
    UNION ALL SELECT 3, '06:00:00', '08:00:00', '0'
    UNION ALL SELECT 3, '08:00:00', '10:00:00', '1'
    UNION ALL SELECT 3, '10:00:00', '12:00:00', '1'
    UNION ALL SELECT 3, '14:00:00', '16:00:00', '1'
    UNION ALL SELECT 3, '16:00:00', '18:00:00', '0'
) t
CROSS JOIN (
    -- 未来7天
    SELECT 0 as days UNION ALL
    SELECT 1 UNION ALL
    SELECT 2 UNION ALL
    SELECT 3 UNION ALL
    SELECT 4 UNION ALL
    SELECT 5 UNION ALL
    SELECT 6
) d;

-- 3. 验证插入结果
-- --------------------
-- 总体统计
SELECT
    '总体统计' as type,
    COUNT(*) as total_slots,
    COUNT(DISTINCT DATE(slot_date)) as days,
    COUNT(DISTINCT lane_id) as lanes,
    MIN(DATE(slot_date)) as from_date,
    MAX(DATE(slot_date)) as to_date
FROM sp_time_slot
WHERE DATE(slot_date) >= @today

UNION ALL

-- 今日时段
SELECT
    '今日时段' as type,
    COUNT(*) as total_slots,
    1 as days,
    COUNT(DISTINCT lane_id) as lanes,
    @today as from_date,
    @today as to_date
FROM sp_time_slot
WHERE DATE(slot_date) = @today;

-- 4. 查看今天的时段详情（H5将返回的数据）
-- --------------------
SELECT
    ts.id,
    l.lane_no,
    l.lane_type,
    CASE l.lane_type
        WHEN '0' THEN '快速'
        WHEN '1' THEN '中速'
        WHEN '2' THEN '慢速'
        ELSE '未知'
    END as lane_type_name,
    DATE_FORMAT(ts.start_time, '%H:%i') as start_time,
    DATE_FORMAT(ts.end_time, '%H:%i') as end_time,
    CASE ts.slot_type
        WHEN '0' THEN '低峰期'
        WHEN '1' THEN '高峰期'
        ELSE '未知'
    END as slot_type_name,
    ts.capacity,
    ts.booked_count,
    (ts.capacity - ts.booked_count) as remaining_count,
    ts.price / 100 as price_yuan
FROM sp_time_slot ts
LEFT JOIN sp_lane l ON ts.lane_id = l.id
WHERE DATE(ts.slot_date) = @today
    AND ts.is_available = '1'
ORDER BY ts.start_time, l.sort;

-- 5. 快速插入今日时段（用于快速测试）
-- 如果只想测试今天，可以只运行下面这部分：
-- --------------------
-- DELETE FROM sp_time_slot WHERE DATE(slot_date) = CURDATE();
--
-- INSERT INTO sp_time_slot (lane_id, slot_date, start_time, end_time, slot_type, is_available, capacity, booked_count, price, create_time, update_time)
-- SELECT
--     lane_id,
--     CURDATE() as slot_date,
--     start_time,
--     end_time,
--     slot_type,
--     '1' as is_available,
--     CASE lane_id WHEN 1 THEN 10 WHEN 2 THEN 8 ELSE 6 END as capacity,
--     0 as booked_count,
--     CASE WHEN slot_type = '1' THEN 5000 ELSE 3000 END as price,
--     NOW() as create_time,
--     NOW() as update_time
-- FROM (
--     SELECT 1 as lane_id, '08:00:00' as start_time, '10:00:00' as end_time, '1' as slot_type
--     UNION ALL SELECT 1, '10:00:00', '12:00:00', '1'
--     UNION ALL SELECT 1, '14:00:00', '16:00:00', '1'
--     UNION ALL SELECT 2, '08:00:00', '10:00:00', '1'
--     UNION ALL SELECT 2, '10:00:00', '12:00:00', '1'
--     UNION ALL SELECT 2, '14:00:00', '16:00:00', '1'
--     UNION ALL SELECT 3, '08:00:00', '10:00:00', '1'
--     UNION ALL SELECT 3, '10:00:00', '12:00:00', '1'
--     UNION ALL SELECT 3, '14:00:00', '16:00:00', '1'
-- ) t;

-- ===================================================================
-- 使用说明：
-- 1. 直接执行整个脚本，会创建3条泳道和未来7天的时段数据
-- 2. 执行后查看"第4步"的结果，确认今天有可预约时段
-- 3. 刷新H5页面，应该能看到时段列表
-- ===================================================================
