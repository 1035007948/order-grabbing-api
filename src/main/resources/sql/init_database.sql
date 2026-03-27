-- =============================================
-- Order Grabbing System Database Initialization
-- =============================================

-- Create database
CREATE DATABASE IF NOT EXISTS `order_grabbing` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `order_grabbing`;

-- =============================================
-- Table: grab_orders
-- Description: Stores grab order information
-- =============================================
CREATE TABLE IF NOT EXISTS `grab_orders` (
    `grab_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary key, auto-incrementing grab order ID',
    `start_time` DATETIME NOT NULL COMMENT 'Grab start time',
    `end_time` DATETIME NOT NULL COMMENT 'Grab end time',
    `product_name` VARCHAR(100) NOT NULL COMMENT 'Product name for grabbing',
    `stock` INT NOT NULL COMMENT 'Available stock for grabbing',
    PRIMARY KEY (`grab_id`),
    INDEX `idx_grab_time` (`start_time`, `end_time`) COMMENT 'Index for time range queries'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Grab order information table';

-- =============================================
-- Table: orders
-- Description: Stores user order information
-- =============================================
CREATE TABLE IF NOT EXISTS `orders` (
    `order_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary key, auto-incrementing order ID',
    `phone_number` VARCHAR(20) NOT NULL COMMENT 'User phone number',
    `grab_id` BIGINT NOT NULL COMMENT 'Foreign key referencing grab_orders.grab_id',
    `order_status` VARCHAR(20) NOT NULL COMMENT 'Order status (e.g., SUCCESS, FAILED)',
    `create_time` DATETIME NOT NULL COMMENT 'Order creation time',
    PRIMARY KEY (`order_id`),
    INDEX `idx_grab_id` (`grab_id`) COMMENT 'Index for grab order ID queries',
    INDEX `idx_phone_number` (`phone_number`) COMMENT 'Index for phone number queries',
    CONSTRAINT `fk_orders_grab_id` FOREIGN KEY (`grab_id`) REFERENCES `grab_orders` (`grab_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='User order information table';

-- =============================================
-- Sample Data Insertion
-- =============================================

-- Insert sample grab orders
INSERT INTO `grab_orders` (`start_time`, `end_time`, `product_name`, `stock`) VALUES
('2026-01-01 10:00:00', '2026-01-01 12:00:00', 'iPhone 15 Pro Max', 100),
('2026-01-01 14:00:00', '2026-01-01 16:00:00', 'MacBook Pro 14"', 50),
('2026-01-02 09:00:00', '2026-01-02 11:00:00', 'AirPods Pro 2', 200),
('2026-01-02 15:00:00', '2026-01-02 17:00:00', 'iPad Air', 75);

-- Insert sample orders (if any user has grabbed)
-- INSERT INTO `orders` (`phone_number`, `grab_id`, `order_status`, `create_time`) VALUES
-- ('13800138001', 1, 'SUCCESS', '2026-01-01 10:05:30'),
-- ('13800138002', 1, 'SUCCESS', '2026-01-01 10:06:15');

-- =============================================
-- Database User Creation (Optional)
-- =============================================
-- Create a dedicated database user for the application
-- CREATE USER IF NOT EXISTS 'order_grabbing_user'@'localhost' IDENTIFIED BY 'Order@Grabbing123';
-- GRANT ALL PRIVILEGES ON `order_grabbing`.* TO 'order_grabbing_user'@'localhost';
-- FLUSH PRIVILEGES;

-- =============================================
-- Verification Queries
-- =============================================
-- Check created tables
-- SHOW TABLES;

-- Check table structures
-- DESC grab_orders;
-- DESC orders;

-- Check sample data
-- SELECT * FROM grab_orders;
-- SELECT * FROM orders;
