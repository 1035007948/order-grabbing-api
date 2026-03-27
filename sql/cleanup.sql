-- =============================================
-- 抢单系统数据库清理脚本
-- 警告: 此脚本会删除所有数据，请谨慎使用
-- =============================================

USE order_grabbing;

SET FOREIGN_KEY_CHECKS = 0;

-- 删除表
DROP TABLE IF EXISTS `order_status_log`;
DROP TABLE IF EXISTS `orders`;
DROP TABLE IF EXISTS `user_roles`;
DROP TABLE IF EXISTS `users`;
DROP TABLE IF EXISTS `grab_order`;

-- 删除视图
DROP VIEW IF EXISTS `v_order_detail`;
DROP VIEW IF EXISTS `v_active_grab_order`;

-- 删除存储过程
DROP PROCEDURE IF EXISTS `sp_create_order`;
DROP PROCEDURE IF EXISTS `sp_cancel_order`;

-- 删除触发器
DROP TRIGGER IF EXISTS `trg_order_status_change`;

SET FOREIGN_KEY_CHECKS = 1;

SELECT '数据库清理完成!' AS message;
