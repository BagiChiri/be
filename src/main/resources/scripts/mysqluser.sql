DROP DATABASE IF EXISTS point_of_sale_db;
DROP USER IF EXISTS `admin`@`%`;
DROP USER IF EXISTS `operator`@`%`;
CREATE DATABASE IF NOT EXISTS point_of_sale_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS `admin`@`%` IDENTIFIED WITH mysql_native_password BY 'admin@123';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, REFERENCES, INDEX, ALTER, EXECUTE, CREATE VIEW, SHOW VIEW,
CREATE ROUTINE, ALTER ROUTINE, EVENT, TRIGGER ON `point_of_sale_db`.* TO `admin`@`%`;
CREATE USER IF NOT EXISTS `operator`@`%` IDENTIFIED WITH mysql_native_password BY 'operator@123';
GRANT SELECT, INSERT, UPDATE, DELETE, SHOW VIEW ON `point_of_sale_db`.* TO `operator`@`%`;
FLUSH PRIVILEGES;

GRANT ALTER ON point_of_sale_db.* TO 'operator'@'localhost';
FLUSH PRIVILEGES;

#
# SHOW GRANTS FOR 'admin'@'%';
