-- =============================================
-- Order Grabbing System Database Cleanup
-- Use with caution! This will delete all data.
-- =============================================

USE `order_grabbing`;

-- Drop foreign key constraints first
ALTER TABLE `orders` DROP FOREIGN KEY IF EXISTS `fk_orders_grab_id`;

-- Drop tables
DROP TABLE IF EXISTS `orders`;
DROP TABLE IF EXISTS `grab_orders`;

-- Optional: Drop the database
-- DROP DATABASE IF EXISTS `order_grabbing`;

-- Optional: Drop the user
-- DROP USER IF EXISTS 'order_grabbing_user'@'localhost';
-- FLUSH PRIVILEGES;
