SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for db_account
-- ----------------------------
DROP TABLE IF EXISTS `db_account`;
CREATE TABLE `db_account` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `clients` json DEFAULT NULL,
  `register_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_email` (`email`),
  UNIQUE KEY `unique_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- add test account
INSERT INTO db_account(username, email, password, role, register_time)
VALUES ('test', 'test@gmail.com', '$2a$10$ZEqNgKnrU.6LG6DKObpbYegISMRJPoE4oEvfpAqWD009EvRwNJ9q6', 'admit', NOW());
-- ----------------------------
-- Table structure for db_client
-- ----------------------------
DROP TABLE IF EXISTS `db_client`;
CREATE TABLE `db_client` (
  `id` int NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `node` varchar(255) DEFAULT NULL,
  `register_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for db_client_detail
-- ----------------------------
DROP TABLE IF EXISTS `db_client_detail`;
CREATE TABLE `db_client_detail` (
  `id` int NOT NULL,
  `os_arch` varchar(255) DEFAULT NULL,
  `os_name` varchar(255) DEFAULT NULL,
  `os_version` varchar(255) DEFAULT NULL,
  `os_bit` int DEFAULT NULL,
  `cpu_name` varchar(255) DEFAULT NULL,
  `cpu_core` int DEFAULT NULL,
  `memory` double DEFAULT NULL,
  `disk` double DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for db_client_ssh
-- ----------------------------
DROP TABLE IF EXISTS `db_client_ssh`;
CREATE TABLE `db_client_ssh` (
  `id` int NOT NULL,
  `port` int DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;
