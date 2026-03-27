-- =============================================
-- 抢单系统测试数据脚本
-- =============================================

USE order_grabbing;

-- 清理旧数据
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE `orders`;
TRUNCATE TABLE `order_status_log`;
TRUNCATE TABLE `user_roles`;
TRUNCATE TABLE `users`;
TRUNCATE TABLE `grab_order`;
SET FOREIGN_KEY_CHECKS = 1;

-- 插入测试用户 (密码都是: password123)
-- BCrypt hash for 'password123': $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.iW8jY9l5LhFQWlGmS6
INSERT INTO `users` (`username`, `password`) VALUES 
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.iW8jY9l5LhFQWlGmS6'),
('user1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.iW8jY9l5LhFQWlGmS6'),
('user2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.iW8jY9l5LhFQWlGmS6'),
('user3', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.iW8jY9l5LhFQWlGmS6');

-- 插入用户角色
INSERT INTO `user_roles` (`user_id`, `role`) VALUES 
(1, 'ROLE_ADMIN'),
(1, 'ROLE_USER'),
(2, 'ROLE_USER'),
(3, 'ROLE_USER'),
(4, 'ROLE_USER');

-- 插入抢单活动
INSERT INTO `grab_order` (`start_time`, `end_time`, `product_name`, `stock`) VALUES 
-- 已结束的活动
(DATE_SUB(NOW(), INTERVAL 3 HOUR), DATE_SUB(NOW(), INTERVAL 1 HOUR), '已结束商品-测试用', 0),
-- 进行中的活动
(DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_ADD(NOW(), INTERVAL 1 HOUR), 'iPhone 15 Pro Max 256GB', 50),
(DATE_SUB(NOW(), INTERVAL 30 MINUTE), DATE_ADD(NOW(), INTERVAL 2 HOUR), 'MacBook Pro 14寸 M3芯片', 30),
(DATE_SUB(NOW(), INTERVAL 10 MINUTE), DATE_ADD(NOW(), INTERVAL 3 HOUR), 'AirPods Pro 第二代', 100),
-- 即将开始的活动
(DATE_ADD(NOW(), INTERVAL 1 HOUR), DATE_ADD(NOW(), INTERVAL 4 HOUR), 'iPad Pro 12.9寸', 20),
(DATE_ADD(NOW(), INTERVAL 2 HOUR), DATE_ADD(NOW(), INTERVAL 5 HOUR), 'Apple Watch Ultra 2', 40);

-- 插入测试订单
INSERT INTO `orders` (`phone`, `grab_order_id`, `status`, `create_time`) VALUES 
('13800138001', 2, 'SUCCESS', DATE_SUB(NOW(), INTERVAL 45 MINUTE)),
('13800138002', 2, 'SUCCESS', DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
('13800138003', 2, 'CANCELLED', DATE_SUB(NOW(), INTERVAL 20 MINUTE)),
('13800138001', 3, 'SUCCESS', DATE_SUB(NOW(), INTERVAL 15 MINUTE)),
('13800138004', 3, 'SUCCESS', DATE_SUB(NOW(), INTERVAL 10 MINUTE)),
('13800138005', 4, 'SUCCESS', DATE_SUB(NOW(), INTERVAL 5 MINUTE));

-- 验证数据
SELECT '=== 用户统计 ===' AS info;
SELECT COUNT(*) AS user_count FROM `users`;

SELECT '=== 抢单活动统计 ===' AS info;
SELECT 
    COUNT(*) AS total_count,
    SUM(CASE WHEN NOW() BETWEEN start_time AND end_time THEN 1 ELSE 0 END) AS active_count,
    SUM(CASE WHEN stock > 0 THEN 1 ELSE 0 END) AS available_count
FROM `grab_order`;

SELECT '=== 订单统计 ===' AS info;
SELECT 
    COUNT(*) AS total_count,
    SUM(CASE WHEN status = 'SUCCESS' THEN 1 ELSE 0 END) AS success_count,
    SUM(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END) AS cancelled_count
FROM `orders`;

SELECT '测试数据插入完成!' AS message;
