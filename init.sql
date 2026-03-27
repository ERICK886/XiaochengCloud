-- 小程网盘数据库初始化脚本

CREATE DATABASE IF NOT EXISTS xiaocheng_netdisk CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE xiaocheng_netdisk;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `total_capacity` BIGINT DEFAULT 5368709120 COMMENT '总容量（字节，默认5GB）',
  `used_capacity` BIGINT DEFAULT 0 COMMENT '已用容量',
  `member_type` TINYINT DEFAULT 0 COMMENT '会员类型 0-普通 1-会员',
  `member_expire_time` DATETIME DEFAULT NULL COMMENT '会员过期时间',
  `status` TINYINT DEFAULT 0 COMMENT '状态 0-正常 1-禁用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 文件表
CREATE TABLE IF NOT EXISTS `file` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父文件夹ID',
  `file_name` VARCHAR(255) NOT NULL COMMENT '文件名',
  `file_path` VARCHAR(1000) DEFAULT NULL COMMENT '文件路径',
  `file_size` BIGINT DEFAULT 0 COMMENT '文件大小',
  `file_type` VARCHAR(50) DEFAULT NULL COMMENT '文件类型',
  `mime_type` VARCHAR(100) DEFAULT NULL COMMENT 'MIME类型',
  `is_folder` TINYINT DEFAULT 0 COMMENT '是否文件夹 0-文件 1-文件夹',
  `file_hash` VARCHAR(64) DEFAULT NULL COMMENT '文件哈希（用于去重）',
  `storage_path` VARCHAR(500) DEFAULT NULL COMMENT '存储路径',
  `thumbnail` VARCHAR(255) DEFAULT NULL COMMENT '缩略图URL',
  `is_favorite` TINYINT DEFAULT 0 COMMENT '是否收藏',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除',
  `delete_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_parent` (`user_id`, `parent_id`),
  KEY `idx_user_deleted` (`user_id`, `is_deleted`),
  KEY `idx_file_hash` (`file_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件表';

-- 分享表
CREATE TABLE IF NOT EXISTS `share` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `share_code` VARCHAR(32) NOT NULL COMMENT '分享码',
  `user_id` BIGINT NOT NULL COMMENT '分享用户ID',
  `file_ids` VARCHAR(500) NOT NULL COMMENT '分享文件ID列表',
  `share_type` TINYINT NOT NULL COMMENT '分享类型 1-公开 2-密码 3-指定好友',
  `password` VARCHAR(100) DEFAULT NULL COMMENT '分享密码',
  `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `download_count` INT DEFAULT 0 COMMENT '下载次数',
  `status` TINYINT DEFAULT 0 COMMENT '状态 0-有效 1-已取消',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_share_code` (`share_code`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分享表';

-- 设备表
CREATE TABLE IF NOT EXISTS `device` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `device_id` VARCHAR(64) NOT NULL COMMENT '设备唯一标识',
  `device_name` VARCHAR(100) DEFAULT NULL COMMENT '设备名称',
  `device_type` TINYINT DEFAULT NULL COMMENT '设备类型 1-iOS 2-Android 3-PC Web',
  `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `last_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
  `last_location` VARCHAR(200) DEFAULT NULL COMMENT '最后登录位置',
  `status` TINYINT DEFAULT 0 COMMENT '状态 0-正常 1-禁用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_device_id` (`device_id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备表';

-- 回收站表
CREATE TABLE IF NOT EXISTS `recycle` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `file_id` BIGINT NOT NULL COMMENT '原始文件ID',
  `file_name` VARCHAR(255) NOT NULL COMMENT '文件名',
  `file_path` VARCHAR(1000) DEFAULT NULL COMMENT '文件路径',
  `file_size` BIGINT DEFAULT 0 COMMENT '文件大小',
  `file_type` VARCHAR(50) DEFAULT NULL COMMENT '文件类型',
  `delete_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '删除时间',
  `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间（删除时间+30天）',
  PRIMARY KEY (`id`),
  KEY `idx_user_expire` (`user_id`, `expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='回收站表';

-- 文件版本表
CREATE TABLE IF NOT EXISTS `file_version` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `file_id` BIGINT NOT NULL COMMENT '文件ID',
  `version_id` VARCHAR(32) NOT NULL COMMENT '版本标识',
  `file_hash` VARCHAR(64) DEFAULT NULL COMMENT '文件哈希',
  `file_size` BIGINT DEFAULT 0 COMMENT '文件大小',
  `storage_path` VARCHAR(500) DEFAULT NULL COMMENT '存储路径',
  `source` TINYINT DEFAULT 1 COMMENT '来源 1-相册备份 2-视频备份 3-手动上传',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_file` (`file_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件版本表';

-- 好友关系表
CREATE TABLE IF NOT EXISTS `friend` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `friend_id` BIGINT NOT NULL COMMENT '好友用户ID',
  `status` TINYINT DEFAULT 0 COMMENT '状态 0-待确认 1-已添加 2-已拒绝',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='好友关系表';

-- 分片上传任务表
CREATE TABLE IF NOT EXISTS `upload_task` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `upload_id` VARCHAR(64) NOT NULL COMMENT '上传任务ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `file_name` VARCHAR(255) NOT NULL COMMENT '文件名',
  `file_size` BIGINT DEFAULT 0 COMMENT '文件大小',
  `file_hash` VARCHAR(64) DEFAULT NULL COMMENT '文件哈希',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父文件夹ID',
  `chunk_count` INT DEFAULT 0 COMMENT '分片总数',
  `uploaded_chunks` VARCHAR(500) DEFAULT '[]' COMMENT '已上传分片列表',
  `status` TINYINT DEFAULT 0 COMMENT '状态 0-进行中 1-已完成 2-已取消',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_upload_id` (`upload_id`),
  KEY `idx_user_status` (`user_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分片上传任务表';

-- 验证码表
CREATE TABLE IF NOT EXISTS `verify_code` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `target` VARCHAR(50) NOT NULL COMMENT '验证码目标（手机号/邮箱）',
  `code` VARCHAR(10) NOT NULL COMMENT '验证码',
  `type` TINYINT NOT NULL COMMENT '类型 1-登录 2-注册 3-修改密码',
  `ip_address` VARCHAR(50) DEFAULT NULL COMMENT '请求IP',
  `error_count` INT DEFAULT 0 COMMENT '错误次数',
  `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_target_type` (`target`, `type`),
  KEY `idx_expire` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='验证码表';

-- 插入测试用户 (密码: 123456)
INSERT INTO `user` (`username`, `password`, `phone`, `nickname`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138000', '管理员', 0);
