-- ===================================================================
-- 检查所有表并添加缺少的 create_dept 字段
-- ===================================================================

-- 1. 查询所有缺少 create_dept 字段的表
SELECT
    TABLE_NAME AS '表名',
    TABLE_COMMENT AS '表注释'
FROM
    information_schema.TABLES t
WHERE
    t.TABLE_SCHEMA = DATABASE()
    AND t.TABLE_TYPE = 'BASE TABLE'
    AND NOT EXISTS (
        SELECT 1
        FROM information_schema.COLUMNS c
        WHERE c.TABLE_SCHEMA = t.TABLE_SCHEMA
          AND c.TABLE_NAME = t.TABLE_NAME
          AND c.COLUMN_NAME = 'create_dept'
    )
ORDER BY t.TABLE_NAME;

-- ===================================================================
-- 2. 为缺少 create_dept 字段的表添加该字段
-- ===================================================================
-- 注意：请先执行上面的查询语句，确认需要添加的表后再执行下面的语句

-- 动态生成并执行ALTER语句
SET @sql = '';

SELECT
    GROUP_CONCAT(
        CONCAT(
            'ALTER TABLE `', TABLE_NAME,
            '` ADD COLUMN `create_dept` BIGINT(20) NULL DEFAULT NULL COMMENT ''创建部门'' AFTER `create_time`;'
        )
        SEPARATOR '\n'
    ) INTO @sql
FROM
    information_schema.TABLES t
WHERE
    t.TABLE_SCHEMA = DATABASE()
    AND t.TABLE_TYPE = 'BASE TABLE'
    AND NOT EXISTS (
        SELECT 1
        FROM information_schema.COLUMNS c
        WHERE c.TABLE_SCHEMA = t.TABLE_SCHEMA
          AND c.TABLE_NAME = t.TABLE_NAME
          AND c.COLUMN_NAME = 'create_dept'
    );

-- 查看生成的SQL语句
SELECT @sql AS '将要执行的SQL语句';

-- ===================================================================
-- 执行添加字段的SQL（取消注释下面的语句来执行）
-- ===================================================================
-- PREPARE stmt FROM @sql;
-- EXECUTE stmt;
-- DEALLOCATE PREPARE stmt;

-- ===================================================================
-- 3. 验证结果：查询所有包含 create_dept 字段的表
-- ===================================================================
SELECT
    TABLE_NAME AS '表名',
    COLUMN_TYPE AS '字段类型',
    COLUMN_COMMENT AS '字段注释'
FROM
    information_schema.COLUMNS
WHERE
    TABLE_SCHEMA = DATABASE()
    AND COLUMN_NAME = 'create_dept'
ORDER BY TABLE_NAME;

-- ===================================================================
-- 使用说明：
-- 1. 先执行第1步的查询，查看哪些表缺少 create_dept 字段
-- 2. 执行第2步的查询，查看将要执行的SQL语句
-- 3. 如果确认无误，取消注释第2步最后的PREPARE/EXECUTE语句并执行
-- 4. 执行第3步验证结果
-- ===================================================================
