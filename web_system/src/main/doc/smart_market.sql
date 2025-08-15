/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 90300
 Source Host           : localhost:3306
 Source Schema         : smart_market

 Target Server Type    : MySQL
 Target Server Version : 90300
 File Encoding         : 65001

 Date: 15/08/2025 23:35:43
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for cashier_order_items
-- ----------------------------
DROP TABLE IF EXISTS `cashier_order_items`;
CREATE TABLE `cashier_order_items`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `product_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `product_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `specification` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `unit_price` decimal(10, 2) NOT NULL,
  `quantity` int NOT NULL,
  `subtotal` decimal(10, 2) NOT NULL,
  `discount_amount` decimal(10, 2) NULL DEFAULT 0.00,
  `actual_amount` decimal(10, 2) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cashier_order_items
-- ----------------------------

-- ----------------------------
-- Table structure for cashier_orders
-- ----------------------------
DROP TABLE IF EXISTS `cashier_orders`;
CREATE TABLE `cashier_orders`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `member_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `member_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `total_quantity` int NOT NULL DEFAULT 0,
  `total_amount` decimal(10, 2) NOT NULL DEFAULT 0.00,
  `discount_amount` decimal(10, 2) NOT NULL DEFAULT 0.00,
  `payable_amount` decimal(10, 2) NOT NULL DEFAULT 0.00,
  `paid_amount` decimal(10, 2) NOT NULL DEFAULT 0.00,
  `change_amount` decimal(10, 2) NOT NULL DEFAULT 0.00,
  `payment_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'CASH',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'PENDING',
  `cashier_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `cashier_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cashier_orders
-- ----------------------------

-- ----------------------------
-- Table structure for categories
-- ----------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `parent_id` bigint NULL DEFAULT NULL,
  `level` int NOT NULL DEFAULT 1,
  `sort_order` int NOT NULL DEFAULT 0,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'ACTIVE',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of categories
-- ----------------------------
INSERT INTO `categories` VALUES (1, '饮料', NULL, 1, 1, 'ACTIVE', '各类饮品', '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `categories` VALUES (2, '食品', NULL, 1, 2, 'ACTIVE', '各类食品', '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `categories` VALUES (3, '日用品', NULL, 1, 3, 'ACTIVE', '日常用品', '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `categories` VALUES (4, '生鲜', NULL, 1, 4, 'ACTIVE', '新鲜食材', '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `categories` VALUES (5, '零食', NULL, 1, 5, 'ACTIVE', '休闲零食', '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `categories` VALUES (6, '碳酸饮料', 1, 2, 1, 'ACTIVE', '可乐、雪碧等', '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `categories` VALUES (7, '果汁饮料', 1, 2, 2, 'ACTIVE', '各类果汁', '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `categories` VALUES (8, '乳制品', 2, 2, 1, 'ACTIVE', '牛奶、酸奶等', '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `categories` VALUES (9, '方便食品', 2, 2, 2, 'ACTIVE', '方便面、速食等', '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `categories` VALUES (10, '水果', 4, 2, 1, 'ACTIVE', '新鲜水果', '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `categories` VALUES (11, '蔬菜', 4, 2, 2, 'ACTIVE', '新鲜蔬菜', '2025-08-13 15:10:32', '2025-08-13 15:10:32');

-- ----------------------------
-- Table structure for member_levels
-- ----------------------------
DROP TABLE IF EXISTS `member_levels`;
CREATE TABLE `member_levels`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `discount` int NOT NULL DEFAULT 100,
  `min_consumption` decimal(10, 2) NOT NULL DEFAULT 0.00,
  `points_ratio` int NOT NULL DEFAULT 100,
  `benefits` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '#67C23A',
  `status` int NOT NULL DEFAULT 1,
  `is_default` tinyint(1) NOT NULL DEFAULT 0,
  `sort_order` int NOT NULL DEFAULT 1,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of member_levels
-- ----------------------------
INSERT INTO `member_levels` VALUES (1, '普通会员', 100, 0.00, 100, '基础会员权益', '#909399', 1, 1, 1, '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `member_levels` VALUES (2, '银卡会员', 95, 1000.00, 120, '95折优惠，积分1.2倍', '#C0C4CC', 1, 0, 2, '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `member_levels` VALUES (3, '金卡会员', 90, 5000.00, 150, '9折优惠，积分1.5倍', '#F56C6C', 1, 0, 3, '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `member_levels` VALUES (4, '钻石会员', 85, 20000.00, 200, '85折优惠，积分2倍，生日特权', '#E6A23C', 1, 0, 4, '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `member_levels` VALUES (5, '至尊会员', 80, 50000.00, 300, '8折优惠，积分3倍，专属客服', '#67C23A', 1, 0, 5, '2025-08-13 15:10:32', '2025-08-13 15:10:32');

-- ----------------------------
-- Table structure for members
-- ----------------------------
DROP TABLE IF EXISTS `members`;
CREATE TABLE `members`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `member_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `gender` int NOT NULL,
  `birthday` date NULL DEFAULT NULL,
  `level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '普通会员',
  `balance` decimal(10, 2) NULL DEFAULT 0.00,
  `points` int NULL DEFAULT 0,
  `total_consumption` decimal(10, 2) NULL DEFAULT 0.00,
  `status` int NOT NULL DEFAULT 1,
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `member_no`(`member_no` ASC) USING BTREE,
  UNIQUE INDEX `phone`(`phone` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of members
-- ----------------------------
INSERT INTO `members` VALUES (1, 'M20250001', '张三', '13900139001', 1, NULL, '金卡会员', 500.00, 1200, 8500.00, 1, '北京市朝阳区', NULL, '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `members` VALUES (2, 'M20250002', '李四', '13900139002', 2, NULL, '银卡会员', 200.00, 800, 3200.00, 1, '上海市浦东新区', NULL, '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `members` VALUES (3, 'M20250003', '王五', '13900139003', 1, NULL, '普通会员', 100.00, 300, 800.00, 1, '广州市天河区', NULL, '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `members` VALUES (4, 'M20250004', '赵六', '13900139004', 2, NULL, '钻石会员', 1000.00, 2500, 25000.00, 1, '深圳市南山区', NULL, '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `members` VALUES (5, 'M20250005', '孙七', '13900139005', 1, NULL, '普通会员', 50.00, 150, 500.00, 1, '杭州市西湖区', NULL, '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `members` VALUES (6, 'M20250006', '周八', '13900139006', 2, NULL, '至尊会员', 2000.00, 5000, 60000.00, 1, '成都市锦江区', NULL, '2025-08-13 15:10:32', '2025-08-13 15:10:32');

-- ----------------------------
-- Table structure for order_items
-- ----------------------------
DROP TABLE IF EXISTS `order_items`;
CREATE TABLE `order_items`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `product_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `product_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `unit_price` decimal(10, 2) NOT NULL,
  `quantity` int NOT NULL,
  `discount_rate` decimal(5, 4) NOT NULL DEFAULT 1.0000,
  `discount_amount` decimal(10, 2) NOT NULL DEFAULT 0.00,
  `subtotal` decimal(12, 2) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_items
-- ----------------------------
INSERT INTO `order_items` VALUES (1, 1, 1, '可口可乐 330ml', 'P001', 3.50, 1, 1.0000, 0.00, 3.50, '2025-08-15 20:31:11');
INSERT INTO `order_items` VALUES (2, 1, 2, '百事可乐 330ml', 'P002', 3.40, 1, 1.0000, 0.00, 3.40, '2025-08-15 20:31:11');
INSERT INTO `order_items` VALUES (3, 2, 1, '可口可乐 330ml', 'P001', 3.50, 1, 1.0000, 0.00, 3.50, '2025-08-15 20:31:26');
INSERT INTO `order_items` VALUES (4, 3, 2, '百事可乐 330ml', 'P002', 3.40, 1, 1.0000, 0.00, 3.40, '2025-08-15 20:32:18');
INSERT INTO `order_items` VALUES (5, 4, 4, '康师傅红烧牛肉面', 'P004', 5.50, 1, 1.0000, 0.00, 5.50, '2025-08-15 21:21:02');

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `member_id` bigint NULL DEFAULT NULL,
  `cashier_id` bigint NOT NULL,
  `total_amount` decimal(10, 2) NOT NULL,
  `discount_amount` decimal(10, 2) NOT NULL DEFAULT 0.00,
  `final_amount` decimal(10, 2) NOT NULL,
  `payment_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'COMPLETED',
  `points_earned` int NULL DEFAULT 0,
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `cashier_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `customer_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_orders_cashier_id`(`cashier_id` ASC) USING BTREE,
  INDEX `idx_orders_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, 'POS20250815203111468', NULL, 0, 6.90, 0.00, 6.90, 'CASH', 'COMPLETED', 0, NULL, '2025-08-15 20:31:11', '2025-08-15 20:31:11', 'SYSTEM', NULL);
INSERT INTO `orders` VALUES (2, 'POS20250815203126877', NULL, 0, 3.50, 0.00, 3.50, 'CASH', 'COMPLETED', 0, NULL, '2025-08-15 20:31:26', '2025-08-15 20:31:26', 'SYSTEM', NULL);
INSERT INTO `orders` VALUES (3, 'POS20250815203218879', NULL, 0, 3.40, 0.00, 3.40, 'WECHAT', 'CANCELLED', 0, NULL, '2025-08-15 20:32:18', '2025-08-15 21:51:54', 'SYSTEM', NULL);
INSERT INTO `orders` VALUES (4, 'POS20250815212101636', NULL, 0, 5.50, 0.00, 5.50, 'CASH', 'COMPLETED', 0, NULL, '2025-08-15 21:21:02', '2025-08-15 21:21:02', 'SYSTEM', '张三');

-- ----------------------------
-- Table structure for products
-- ----------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `category_id` bigint NOT NULL,
  `supplier_id` bigint NULL DEFAULT NULL,
  `barcode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '个',
  `specification` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `purchase_price` decimal(10, 2) NOT NULL DEFAULT 0.00,
  `sale_price` decimal(10, 2) NOT NULL DEFAULT 0.00,
  `member_price` decimal(10, 2) NULL DEFAULT NULL,
  `min_stock` int NOT NULL DEFAULT 0,
  `max_stock` int NOT NULL DEFAULT 1000,
  `current_stock` int NOT NULL DEFAULT 0,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'ACTIVE',
  `image_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `code`(`code` ASC) USING BTREE,
  UNIQUE INDEX `barcode`(`barcode` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of products
-- ----------------------------
INSERT INTO `products` VALUES (1, 'P001', '可口可乐 330ml', 6, 1, '6901234567890', '瓶', '330ml/瓶', 2.50, 3.50, 3.20, 50, 500, 198, 'ACTIVE', NULL, NULL, '2025-08-13 15:10:32', '2025-08-15 20:31:26');
INSERT INTO `products` VALUES (2, 'P002', '百事可乐 330ml', 6, 2, '6901234567891', '瓶', '330ml/瓶', 2.40, 3.40, 3.10, 50, 500, 179, 'ACTIVE', NULL, NULL, '2025-08-13 15:10:32', '2025-08-15 21:51:54');
INSERT INTO `products` VALUES (3, 'P003', '蒙牛纯牛奶 250ml', 8, 3, '6901234567892', '盒', '250ml/盒', 3.20, 4.50, 4.20, 30, 300, 120, 'ACTIVE', NULL, NULL, '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `products` VALUES (4, 'P004', '康师傅红烧牛肉面', 9, 4, '6901234567893', '袋', '120g/袋', 3.80, 5.50, 5.20, 20, 200, 79, 'ACTIVE', NULL, NULL, '2025-08-13 15:10:32', '2025-08-15 21:21:02');
INSERT INTO `products` VALUES (5, 'P005', '统一绿茶 500ml', 7, 5, '6901234567894', '瓶', '500ml/瓶', 2.80, 4.00, 3.70, 40, 400, 150, 'ACTIVE', NULL, NULL, '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `products` VALUES (6, 'P006', '苹果', 10, NULL, '6901234567895', 'kg', '新鲜苹果', 8.00, 12.00, 11.50, 10, 100, 50, 'ACTIVE', NULL, NULL, '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `products` VALUES (7, 'P007', '香蕉', 10, NULL, '6901234567896', 'kg', '进口香蕉', 6.50, 9.80, 9.50, 15, 150, 60, 'ACTIVE', NULL, NULL, '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `products` VALUES (8, 'P008', '白菜', 11, NULL, '6901234567897', 'kg', '新鲜白菜', 2.00, 3.50, 3.20, 20, 200, 80, 'ACTIVE', NULL, NULL, '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `products` VALUES (9, 'P009', '土豆', 11, NULL, '6901234567898', 'kg', '新鲜土豆', 2.50, 4.00, 3.80, 25, 250, 100, 'ACTIVE', NULL, NULL, '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `products` VALUES (10, 'P010', '矿泉水 550ml', 1, 1, '6901234567899', '瓶', '550ml/瓶', 1.20, 2.00, 1.80, 100, 1000, 500, 'ACTIVE', NULL, NULL, '2025-08-13 15:10:32', '2025-08-13 15:10:32');

-- ----------------------------
-- Table structure for stock_in_records
-- ----------------------------
DROP TABLE IF EXISTS `stock_in_records`;
CREATE TABLE `stock_in_records`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `record_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `product_id` bigint NOT NULL,
  `supplier_id` bigint NULL DEFAULT NULL,
  `quantity` int NOT NULL,
  `unit_price` decimal(10, 2) NOT NULL,
  `total_amount` decimal(12, 2) NOT NULL,
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'PURCHASE',
  `operator_id` bigint NOT NULL,
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `record_no`(`record_no` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of stock_in_records
-- ----------------------------

-- ----------------------------
-- Table structure for stock_out_records
-- ----------------------------
DROP TABLE IF EXISTS `stock_out_records`;
CREATE TABLE `stock_out_records`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `record_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `product_id` bigint NOT NULL,
  `quantity` int NOT NULL,
  `unit_price` decimal(10, 2) NOT NULL,
  `total_amount` decimal(12, 2) NOT NULL,
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'SALE',
  `reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `operator_id` bigint NOT NULL,
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `record_no`(`record_no` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of stock_out_records
-- ----------------------------

-- ----------------------------
-- Table structure for supplier_categories
-- ----------------------------
DROP TABLE IF EXISTS `supplier_categories`;
CREATE TABLE `supplier_categories`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `category_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `sort_order` int NOT NULL DEFAULT 0,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'ACTIVE',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `category_code`(`category_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of supplier_categories
-- ----------------------------
INSERT INTO `supplier_categories` VALUES (1, 'FOOD', '食品供应商', '提供各类食品的供应商', 1, 'ACTIVE', '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `supplier_categories` VALUES (2, 'BEVERAGE', '饮料供应商', '提供各类饮料的供应商', 2, 'ACTIVE', '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `supplier_categories` VALUES (3, 'DAILY', '日用品供应商', '提供日常用品的供应商', 3, 'ACTIVE', '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `supplier_categories` VALUES (4, 'FRESH', '生鲜供应商', '提供新鲜蔬果肉类的供应商', 4, 'ACTIVE', '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `supplier_categories` VALUES (5, 'SNACK', '零食供应商', '提供各类零食的供应商', 5, 'ACTIVE', '2025-08-13 15:10:32', '2025-08-13 15:10:32');

-- ----------------------------
-- Table structure for suppliers
-- ----------------------------
DROP TABLE IF EXISTS `suppliers`;
CREATE TABLE `suppliers`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `supplier_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `supplier_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `contact_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'ACTIVE',
  `credit_limit` decimal(10, 2) NULL DEFAULT NULL,
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `supplier_code`(`supplier_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of suppliers
-- ----------------------------
INSERT INTO `suppliers` VALUES (1, 'SUP001', '可口可乐公司', '张经理', '13800138100', 'zhang@coca-cola.com', '上海市浦东新区', '饮料供应商', 'ACTIVE', 100000.00, NULL, '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `suppliers` VALUES (2, 'SUP002', '百事可乐公司', '李经理', '13800138101', 'li@pepsi.com', '北京市朝阳区', '饮料供应商', 'ACTIVE', 80000.00, NULL, '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `suppliers` VALUES (3, 'SUP003', '蒙牛乳业', '王经理', '13800138102', 'wang@mengniu.com', '内蒙古呼和浩特', '食品供应商', 'ACTIVE', 120000.00, NULL, '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `suppliers` VALUES (4, 'SUP004', '康师傅食品', '刘经理', '13800138103', 'liu@kangshifu.com', '天津市滨海新区', '食品供应商', 'ACTIVE', 90000.00, NULL, '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `suppliers` VALUES (5, 'SUP005', '统一企业', '陈经理', '13800138104', 'chen@uni-president.com', '上海市闵行区', '食品供应商', 'ACTIVE', 85000.00, NULL, '2025-08-13 15:10:32', '2025-08-13 15:10:32');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'CASHIER',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'ACTIVE',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `phone`(`phone` ASC) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGkqx8Ey4NZQQ2Gg6Uw8Iy2Iq', '系统管理员', '13800138000', 'admin@example.com', 'ADMIN', 'ACTIVE', '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `users` VALUES (2, 'manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGkqx8Ey4NZQQ2Gg6Uw8Iy2Iq', '店长', '13800138001', 'manager@example.com', 'MANAGER', 'ACTIVE', '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `users` VALUES (3, 'cashier01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGkqx8Ey4NZQQ2Gg6Uw8Iy2Iq', '张三', '13800138002', 'cashier01@example.com', 'CASHIER', 'ACTIVE', '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `users` VALUES (4, 'cashier02', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGkqx8Ey4NZQQ2Gg6Uw8Iy2Iq', '李四', '13800138003', 'cashier02@example.com', 'CASHIER', 'ACTIVE', '2025-08-13 15:10:32', '2025-08-13 15:10:32');
INSERT INTO `users` VALUES (5, 'warehouse', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGkqx8Ey4NZQQ2Gg6Uw8Iy2Iq', '仓库管理员', '13800138004', 'warehouse@example.com', 'WAREHOUSE', 'ACTIVE', '2025-08-13 15:10:32', '2025-08-13 15:10:32');

SET FOREIGN_KEY_CHECKS = 1;
