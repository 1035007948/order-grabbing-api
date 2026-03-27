-- ============================================
-- 抢单系统测试数据脚本
-- ============================================

USE order_grabbing;

-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 清空现有数据
TRUNCATE TABLE `orders`;
TRUNCATE TABLE `grab_orders`;
TRUNCATE TABLE `users`;

-- 启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 插入测试用户 (密码均为: password123)
-- ============================================
INSERT INTO `users` (`username`, `password`, `phone`) VALUES 
('user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13800000001'),
('user2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13800000002'),
('user3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13800000003');

-- ============================================
-- 插入测试抢单活动
-- ============================================
INSERT INTO `grab_orders` (`product_name`, `stock`, `start_time`, `end_time`) VALUES 
-- 进行中的活动
('iPhone 15 Pro', 10, DATE_ADD(NOW(), INTERVAL -1 HOUR), DATE_ADD(NOW(), INTERVAL 2 HOUR)),
('小米14 Ultra', 20, DATE_ADD(NOW(), INTERVAL -30 MINUTE), DATE_ADD(NOW(), INTERVAL 3 HOUR)),
('华为Mate 60 Pro', 5, DATE_ADD(NOW(), INTERVAL -2 HOUR), DATE_ADD(NOW(), INTERVAL 1 HOUR)),

-- 即将开始的活动
('MacBook Pro 14', 8, DATE_ADD(NOW(), INTERVAL 1 HOUR), DATE_ADD(NOW(), INTERVAL 4 HOUR)),
('iPad Pro 12.9', 15, DATE_ADD(NOW(), INTERVAL 2 HOUR), DATE_ADD(NOW(), INTERVAL 5 HOUR)),

-- 已结束的活动
('AirPods Pro 2', 0, DATE_ADD(NOW(), INTERVAL -3 HOUR), DATE_ADD(NOW(), INTERVAL -1 HOUR));

-- ============================================
-- 插入测试订单
-- ============================================
INSERT INTO `orders` (`phone`, `grab_id`, `status`, `create_time`) VALUES 
('13800000001', 1, 'GRABBED', DATE_ADD(NOW(), INTERVAL -30 MINUTE)),
('13800000002', 1, 'GRABBED', DATE_ADD(NOW(), INTERVAL -20 MINUTE)),
('13800000001', 2, 'GRABBED', DATE_ADD(NOW(), INTERVAL -15 MINUTE)),
('13800000003', 3, 'COMPLETED', DATE_ADD(NOW(), INTERVAL -1 HOUR)),
('13800000002', 6, 'CANCELLED', DATE_ADD(NOW(), INTERVAL -2 HOUR));

SELECT '测试数据插入完成!' AS message;

-- 显示统计
SELECT 
    (SELECT COUNT(*) FROM users) AS user_count,
    (SELECT COUNT(*) FROM grab_orders) AS grab_order_count,
    (SELECT COUNT(*) FROM orders) AS order_count;
