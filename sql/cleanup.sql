-- ============================================
-- 抢单系统数据库清理脚本
-- 警告: 此脚本会删除所有数据，请谨慎使用
-- ============================================

USE order_grabbing;

-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 清空订单表
TRUNCATE TABLE `orders`;

-- 清空抢单活动表
TRUNCATE TABLE `grab_orders`;

-- 清空用户表
TRUNCATE TABLE `users`;

-- 启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

SELECT '数据清理完成!' AS message;
