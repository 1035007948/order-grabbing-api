-- ============================================
-- 抢单系统数据库部署脚本
-- 数据库: MySQL 5.7+
-- 作者: System
-- 创建时间: 2024
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS order_grabbing DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE order_grabbing;

-- ============================================
-- 用户表
-- ============================================
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码(加密)',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================
-- 抢单活动表
-- ============================================
DROP TABLE IF EXISTS `grab_orders`;
CREATE TABLE `grab_orders` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '抢单ID',
    `product_name` VARCHAR(100) NOT NULL COMMENT '商品名称',
    `stock` INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    `start_time` DATETIME NOT NULL COMMENT '抢单开始时间',
    `end_time` DATETIME NOT NULL COMMENT '抢单结束时间',
    PRIMARY KEY (`id`),
    KEY `idx_time_range` (`start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='抢单活动表';

-- ============================================
-- 订单表
-- ============================================
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
    `grab_id` BIGINT DEFAULT NULL COMMENT '抢单ID',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '订单状态: PENDING-待处理, GRABBED-已抢到, CANCELLED-已取消, COMPLETED-已完成',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_phone` (`phone`),
    KEY `idx_grab_id` (`grab_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_orders_grab_id` FOREIGN KEY (`grab_id`) REFERENCES `grab_orders` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- ============================================
-- 初始化管理员用户 (密码: admin123)
-- ============================================
INSERT INTO `users` (`username`, `password`, `phone`) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13800000000');

-- ============================================
-- 初始化测试抢单活动
-- ============================================
INSERT INTO `grab_orders` (`product_name`, `stock`, `start_time`, `end_time`) VALUES 
('测试商品A', 100, DATE_ADD(NOW(), INTERVAL -1 HOUR), DATE_ADD(NOW(), INTERVAL 1 HOUR)),
('测试商品B', 50, DATE_ADD(NOW(), INTERVAL 1 HOUR), DATE_ADD(NOW(), INTERVAL 3 HOUR));

-- ============================================
-- 创建视图: 活跃抢单活动视图
-- ============================================
DROP VIEW IF EXISTS `v_active_grab_orders`;
CREATE VIEW `v_active_grab_orders` AS
SELECT 
    id,
    product_name,
    stock,
    start_time,
    end_time,
    CASE 
        WHEN NOW() BETWEEN start_time AND end_time AND stock > 0 THEN 1
        ELSE 0
    END AS is_active
FROM `grab_orders`
WHERE end_time > NOW();

-- ============================================
-- 创建存储过程: 抢单操作
-- ============================================
DROP PROCEDURE IF EXISTS `sp_grab_order`;
DELIMITER //
CREATE PROCEDURE `sp_grab_order`(
    IN p_phone VARCHAR(20),
    IN p_grab_id BIGINT,
    OUT p_result INT,
    OUT p_message VARCHAR(255)
)
BEGIN
    DECLARE v_stock INT DEFAULT 0;
    DECLARE v_start_time DATETIME;
    DECLARE v_end_time DATETIME;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result = -1;
        SET p_message = '系统错误';
    END;
    
    START TRANSACTION;
    
    -- 检查抢单活动是否存在且有效
    SELECT stock, start_time, end_time INTO v_stock, v_start_time, v_end_time
    FROM `grab_orders` WHERE id = p_grab_id FOR UPDATE;
    
    IF v_stock IS NULL THEN
        SET p_result = 0;
        SET p_message = '抢单活动不存在';
        ROLLBACK;
    ELSEIF NOW() < v_start_time THEN
        SET p_result = 0;
        SET p_message = '抢单尚未开始';
        ROLLBACK;
    ELSEIF NOW() > v_end_time THEN
        SET p_result = 0;
        SET p_message = '抢单已结束';
        ROLLBACK;
    ELSEIF v_stock <= 0 THEN
        SET p_result = 0;
        SET p_message = '库存不足';
        ROLLBACK;
    ELSE
        -- 扣减库存
        UPDATE `grab_orders` SET stock = stock - 1 WHERE id = p_grab_id;
        
        -- 创建订单
        INSERT INTO `orders` (`phone`, `grab_id`, `status`) VALUES (p_phone, p_grab_id, 'GRABBED');
        
        SET p_result = 1;
        SET p_message = '抢单成功';
        COMMIT;
    END IF;
END //
DELIMITER ;

-- ============================================
-- 创建索引优化查询
-- ============================================
-- 订单按创建时间查询优化
CREATE INDEX idx_orders_create_time_desc ON `orders` (`create_time` DESC);

-- ============================================
-- 授权语句 (根据实际用户名修改)
-- ============================================
-- GRANT SELECT, INSERT, UPDATE, DELETE ON order_grabbing.* TO 'order_user'@'%';
-- FLUSH PRIVILEGES;

-- 完成提示
SELECT '数据库部署完成!' AS message;
