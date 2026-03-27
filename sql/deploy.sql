-- =============================================
-- 抢单系统数据库部署脚本
-- 数据库: MySQL 5.7+ / MySQL 8.0+
-- 作者: doubao
-- 创建时间: 2024
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS order_grabbing DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE order_grabbing;

-- =============================================
-- 1. 用户表
-- =============================================
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =============================================
-- 2. 用户角色关联表
-- =============================================
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles` (
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role` VARCHAR(50) NOT NULL COMMENT '角色名称',
    PRIMARY KEY (`user_id`, `role`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_user_roles_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- =============================================
-- 3. 抢单活动表
-- =============================================
DROP TABLE IF EXISTS `grab_order`;
CREATE TABLE `grab_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '抢单活动ID',
    `start_time` DATETIME NOT NULL COMMENT '抢单开始时间',
    `end_time` DATETIME NOT NULL COMMENT '抢单结束时间',
    `product_name` VARCHAR(200) NOT NULL COMMENT '商品名称',
    `stock` INT NOT NULL DEFAULT 0 COMMENT '商品库存',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_start_time` (`start_time`),
    KEY `idx_end_time` (`end_time`),
    KEY `idx_time_range` (`start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='抢单活动表';

-- =============================================
-- 4. 订单表
-- =============================================
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
    `grab_order_id` BIGINT NOT NULL COMMENT '抢单活动ID',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '订单状态: PENDING-待处理, SUCCESS-成功, FAILED-失败, CANCELLED-已取消',
    `create_time` DATETIME NOT NULL COMMENT '订单创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_phone` (`phone`),
    KEY `idx_grab_order_id` (`grab_order_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_orders_grab_order` FOREIGN KEY (`grab_order_id`) REFERENCES `grab_order` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- =============================================
-- 5. 初始化数据
-- =============================================

-- 插入默认管理员用户 (密码: admin123, BCrypt加密)
INSERT INTO `users` (`username`, `password`) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH');

INSERT INTO `user_roles` (`user_id`, `role`) VALUES 
(1, 'ROLE_ADMIN'),
(1, 'ROLE_USER');

-- 插入测试用户 (密码: user123)
INSERT INTO `users` (`username`, `password`) VALUES 
('user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH');

INSERT INTO `user_roles` (`user_id`, `role`) VALUES 
(2, 'ROLE_USER');

-- 插入示例抢单活动
INSERT INTO `grab_order` (`start_time`, `end_time`, `product_name`, `stock`) VALUES 
(DATE_ADD(NOW(), INTERVAL -1 HOUR), DATE_ADD(NOW(), INTERVAL 1 HOUR), 'iPhone 15 Pro Max', 100),
(DATE_ADD(NOW(), INTERVAL 1 HOUR), DATE_ADD(NOW(), INTERVAL 3 HOUR), 'MacBook Pro 14寸', 50),
(DATE_ADD(NOW(), INTERVAL 2 HOUR), DATE_ADD(NOW(), INTERVAL 5 HOUR), 'AirPods Pro 2', 200);

-- =============================================
-- 6. 创建视图
-- =============================================

-- 订单详情视图
DROP VIEW IF EXISTS `v_order_detail`;
CREATE VIEW `v_order_detail` AS
SELECT 
    o.id AS order_id,
    o.phone,
    o.status,
    o.create_time,
    g.id AS grab_order_id,
    g.product_name,
    g.start_time AS grab_start_time,
    g.end_time AS grab_end_time
FROM `orders` o
LEFT JOIN `grab_order` g ON o.grab_order_id = g.id;

-- 进行中的抢单活动视图
DROP VIEW IF EXISTS `v_active_grab_order`;
CREATE VIEW `v_active_grab_order` AS
SELECT 
    id,
    product_name,
    stock,
    start_time,
    end_time,
    TIMESTAMPDIFF(MINUTE, NOW(), end_time) AS remaining_minutes
FROM `grab_order`
WHERE NOW() BETWEEN start_time AND end_time AND stock > 0;

-- =============================================
-- 7. 创建存储过程
-- =============================================

DELIMITER //

-- 抢单存储过程(带库存检查和并发控制)
DROP PROCEDURE IF EXISTS `sp_create_order`//
CREATE PROCEDURE `sp_create_order`(
    IN p_phone VARCHAR(20),
    IN p_grab_order_id BIGINT,
    OUT p_result INT,
    OUT p_message VARCHAR(255)
)
BEGIN
    DECLARE v_stock INT DEFAULT 0;
    DECLARE v_start_time DATETIME;
    DECLARE v_end_time DATETIME;
    DECLARE v_order_id BIGINT;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result = -1;
        SET p_message = '系统异常，请稍后重试';
    END;
    
    START TRANSACTION;
    
    -- 查询抢单活动信息(加行锁)
    SELECT stock, start_time, end_time 
    INTO v_stock, v_start_time, v_end_time
    FROM `grab_order` 
    WHERE id = p_grab_order_id
    FOR UPDATE;
    
    -- 检查抢单活动是否存在
    IF v_stock IS NULL THEN
        ROLLBACK;
        SET p_result = -2;
        SET p_message = '抢单活动不存在';
    -- 检查是否在活动时间内
    ELSEIF NOW() < v_start_time THEN
        ROLLBACK;
        SET p_result = -3;
        SET p_message = '抢单活动尚未开始';
    ELSEIF NOW() > v_end_time THEN
        ROLLBACK;
        SET p_result = -4;
        SET p_message = '抢单活动已结束';
    -- 检查库存
    ELSEIF v_stock <= 0 THEN
        ROLLBACK;
        SET p_result = -5;
        SET p_message = '商品库存不足';
    ELSE
        -- 扣减库存
        UPDATE `grab_order` SET stock = stock - 1 WHERE id = p_grab_order_id;
        
        -- 创建订单
        INSERT INTO `orders` (phone, grab_order_id, status, create_time)
        VALUES (p_phone, p_grab_order_id, 'SUCCESS', NOW());
        
        SET v_order_id = LAST_INSERT_ID();
        
        COMMIT;
        SET p_result = v_order_id;
        SET p_message = '抢单成功';
    END IF;
END//

-- 取消订单存储过程(恢复库存)
DROP PROCEDURE IF EXISTS `sp_cancel_order`//
CREATE PROCEDURE `sp_cancel_order`(
    IN p_order_id BIGINT,
    OUT p_result INT,
    OUT p_message VARCHAR(255)
)
BEGIN
    DECLARE v_status VARCHAR(20);
    DECLARE v_grab_order_id BIGINT;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result = -1;
        SET p_message = '系统异常，请稍后重试';
    END;
    
    START TRANSACTION;
    
    -- 查询订单信息
    SELECT status, grab_order_id 
    INTO v_status, v_grab_order_id
    FROM `orders` 
    WHERE id = p_order_id
    FOR UPDATE;
    
    IF v_status IS NULL THEN
        ROLLBACK;
        SET p_result = -2;
        SET p_message = '订单不存在';
    ELSEIF v_status = 'CANCELLED' THEN
        ROLLBACK;
        SET p_result = -3;
        SET p_message = '订单已取消';
    ELSE
        -- 更新订单状态
        UPDATE `orders` SET status = 'CANCELLED' WHERE id = p_order_id;
        
        -- 恢复库存
        UPDATE `grab_order` SET stock = stock + 1 WHERE id = v_grab_order_id;
        
        COMMIT;
        SET p_result = 1;
        SET p_message = '订单取消成功';
    END IF;
END//

DELIMITER ;

-- =============================================
-- 8. 创建触发器
-- =============================================

DELIMITER //

-- 订单状态变更日志触发器
DROP TABLE IF EXISTS `order_status_log`;
CREATE TABLE `order_status_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `order_id` BIGINT NOT NULL,
    `old_status` VARCHAR(20),
    `new_status` VARCHAR(20) NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单状态变更日志';

DROP TRIGGER IF EXISTS `trg_order_status_change`//
CREATE TRIGGER `trg_order_status_change`
AFTER UPDATE ON `orders`
FOR EACH ROW
BEGIN
    IF OLD.status != NEW.status THEN
        INSERT INTO `order_status_log` (order_id, old_status, new_status)
        VALUES (NEW.id, OLD.status, NEW.status);
    END IF;
END//

DELIMITER ;

-- =============================================
-- 9. 创建索引优化
-- =============================================

-- 复合索引优化查询性能
CREATE INDEX `idx_orders_phone_status` ON `orders` (`phone`, `status`);
CREATE INDEX `idx_orders_grab_status` ON `orders` (`grab_order_id`, `status`);

-- =============================================
-- 完成
-- =============================================
SELECT '数据库部署完成!' AS message;
