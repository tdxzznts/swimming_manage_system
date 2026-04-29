-- ===================================================================
-- 初始化时段测试数据
-- ===================================================================

-- 1. 先检查现有数据
SELECT
    id,
    lane_id,
    DATE(slot_date) as slot_date,
    start_time,
    end_time,
    slot_type,
    is_available,
    capacity,
    booked_count
FROM sp_time_slot
ORDER BY slot_date, start_time;

-- 2. 清理旧测试数据（可选）
-- DELETE FROM sp_time_slot WHERE DATE(slot_date) >= CURDATE();

-- 3. 插入今天和未来7天的时段数据
-- 注意：需要先确保 sp_lane 表有泳道数据
-- 假设有3条泳道（ID: 1, 2, 3）

-- 设置变量
SET @today = CURDATE();
SET @lane1_id = 1;
SET @lane2_id = 2;
SET @lane3_id = 3;

-- 为每条泳道创建未来7天的时段（每天5个时段：6:00-8:00, 8:00-10:00, 10:00-12:00, 14:00-16:00, 16:00-18:00）
INSERT INTO sp_time_slot (lane_id, slot_date, start_time, end_time, slot_type, is_available, capacity, booked_count, price, create_time, update_time)
SELECT
    lane_id,
    DATE_ADD(@today, INTERVAL days DAY) as slot_date,
    start_time,
    end_time,
    slot_type,
    '1' as is_available,
    10 as capacity,
    0 as booked_count,
    CASE
        WHEN slot_type = '1' THEN 5000  -- 高峰期 50元
        ELSE 3000  -- 低峰期 30元
    END as price,
    NOW() as create_time,
    NOW() as update_time
FROM (
    SELECT
        @lane1_id as lane_id, '06:00:00' as start_time, '08:00:00' as end_time, '0' as slot_type
    UNION ALL SELECT @lane1_id, '08:00:00', '10:00:00', '1'
    UNION ALL SELECT @lane1_id, '10:00:00', '12:00:00', '1'
    UNION ALL SELECT @lane1_id, '14:00:00', '16:00:00', '1'
    UNION ALL SELECT @lane1_id, '16:00:00', '18:00:00', '0'

    UNION ALL SELECT @lane2_id, '06:00:00', '08:00:00', '0'
    UNION ALL SELECT @lane2_id, '08:00:00', '10:00:00', '1'
    UNION ALL SELECT @lane2_id, '10:00:00', '12:00:00', '1'
    UNION ALL SELECT @lane2_id, '14:00:00', '16:00:00', '1'
    UNION ALL SELECT @lane2_id, '16:00:00', '18:00:00', '0'

    UNION ALL SELECT @lane3_id, '06:00:00', '08:00:00', '0'
    UNION ALL SELECT @lane3_id, '08:00:00', '10:00:00', '1'
    UNION ALL SELECT @lane3_id, '10:00:00', '12:00:00', '1'
    UNION ALL SELECT @lane3_id, '14:00:00', '16:00:00', '1'
    UNION ALL SELECT @lane3_id, '16:00:00', '18:00:00', '0'
) t
CROSS JOIN (
    SELECT 0 as days UNION ALL
    SELECT 1 UNION ALL
    SELECT 2 UNION ALL
    SELECT 3 UNION ALL
    SELECT 4 UNION ALL
    SELECT 5 UNION ALL
    SELECT 6
) d;

-- 4. 验证插入结果
SELECT
    COUNT(*) as total_slots,
    COUNT(DISTINCT DATE(slot_date)) as days,
    COUNT(DISTINCT lane_id) as lanes,
    MIN(DATE(slot_date)) as from_date,
    MAX(DATE(slot_date)) as to_date
FROM sp_time_slot
WHERE DATE(slot_date) >= @today;

-- 5. 查看今天的时段列表
SELECT
    ts.id,
    l.lane_no,
    ts.start_time,
    ts.end_time,
    CASE ts.slot_type
        WHEN '0' THEN '低峰期'
        WHEN '1' THEN '高峰期'
        ELSE '未知'
    END as slot_type_name,
    ts.capacity,
    ts.booked_count,
    (ts.capacity - ts.booked_count) as remaining,
    ts.price / 100 as price_yuan
FROM sp_time_slot ts
LEFT JOIN sp_lane l ON ts.lane_id = l.id
WHERE DATE(ts.slot_date) = @today
    AND ts.is_available = '1'
ORDER BY ts.start_time, l.lane_no;

-- ===================================================================
-- 使用说明：
-- 1. 先执行第1步查看现有数据
-- 2. 如果需要，执行第2步清理旧数据
-- 3. 执行第3-4步插入新数据并验证
-- 4. 执行第5步查看今天的时段列表
-- ===================================================================
