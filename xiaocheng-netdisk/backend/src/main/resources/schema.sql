CREATE DATABASE IF NOT EXISTS xiaocheng_netdisk DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE xiaocheng_netdisk;

CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `total_capacity` BIGINT NOT NULL DEFAULT 5368709120 COMMENT '总容量（字节）',
    `used_capacity` BIGINT NOT NULL DEFAULT 0 COMMENT '已用容量',
    `member_type` TINYINT NOT NULL DEFAULT 0 COMMENT '会员类型 0-普通 1-会员',
    `member_expire_time` DATETIME DEFAULT NULL COMMENT '会员过期时间',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态 0-正常 1-禁用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE `file` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `parent_id` BIGINT DEFAULT NULL COMMENT '父文件夹ID',
    `file_name` VARCHAR(255) NOT NULL COMMENT '文件名',
    `file_path` VARCHAR(1000) DEFAULT NULL COMMENT '文件路径',
    `file_size` BIGINT NOT NULL DEFAULT 0 COMMENT '文件大小',
    `file_type` VARCHAR(50) DEFAULT NULL COMMENT '文件类型',
    `mime_type` VARCHAR(100) DEFAULT NULL COMMENT 'MIME类型',
    `is_folder` TINYINT NOT NULL DEFAULT 0 COMMENT '是否文件夹 0-文件 1-文件夹',
    `file_hash` VARCHAR(64) DEFAULT NULL COMMENT '文件哈希',
    `storage_path` VARCHAR(500) DEFAULT NULL COMMENT '存储路径',
    `thumbnail` VARCHAR(255) DEFAULT NULL COMMENT '缩略图URL',
    `is_favorite` TINYINT NOT NULL DEFAULT 0 COMMENT '是否收藏',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    `delete_time` DATETIME DEFAULT NULL COMMENT '删除时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_parent` (`user_id`, `parent_id`),
    KEY `idx_user_deleted` (`user_id`, `is_deleted`),
    KEY `idx_file_hash` (`file_hash`),
    KEY `idx_user_favorite` (`user_id`, `is_favorite`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件表';

CREATE TABLE `share` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `share_code` VARCHAR(32) NOT NULL COMMENT '分享码',
    `user_id` BIGINT NOT NULL COMMENT '分享用户ID',
    `file_ids` VARCHAR(500) NOT NULL COMMENT '分享文件ID列表',
    `share_type` TINYINT NOT NULL DEFAULT 1 COMMENT '分享类型 1-公开 2-密码 3-指定好友',
    `password` VARCHAR(100) DEFAULT NULL COMMENT '分享密码',
    `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间',
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
    `download_count` INT NOT NULL DEFAULT 0 COMMENT '下载次数',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态 0-有效 1-已取消',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_share_code` (`share_code`),
    KEY `idx_user` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分享表';

CREATE TABLE `share_access` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `share_id` BIGINT NOT NULL COMMENT '分享ID',
    `access_user_id` BIGINT DEFAULT NULL COMMENT '访问用户ID',
    `access_type` TINYINT NOT NULL COMMENT '访问类型 1-查看 2-下载',
    `access_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
    `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    `device_info` VARCHAR(200) DEFAULT NULL COMMENT '设备信息',
    PRIMARY KEY (`id`),
    KEY `idx_share` (`share_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分享访问记录表';

CREATE TABLE `backup_task` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `task_type` TINYINT NOT NULL COMMENT '任务类型 1-相册 2-视频 3-文件夹',
    `source_path` VARCHAR(500) DEFAULT NULL COMMENT '源路径',
    `target_path` VARCHAR(500) DEFAULT NULL COMMENT '目标路径',
    `backup_mode` TINYINT NOT NULL DEFAULT 1 COMMENT '备份模式 1-仅WiFi 2-所有网络',
    `quality_mode` TINYINT NOT NULL DEFAULT 1 COMMENT '质量模式 1-原图 2-压缩',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态 0-未启动 1-运行中 2-暂停 3-停止',
    `last_backup_time` DATETIME DEFAULT NULL COMMENT '上次备份时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='备份任务表';

CREATE TABLE `recycle` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `file_id` BIGINT NOT NULL COMMENT '原始文件ID',
    `file_name` VARCHAR(255) NOT NULL COMMENT '文件名',
    `file_path` VARCHAR(1000) DEFAULT NULL COMMENT '文件路径',
    `file_size` BIGINT NOT NULL DEFAULT 0 COMMENT '文件大小',
    `file_type` VARCHAR(50) DEFAULT NULL COMMENT '文件类型',
    `delete_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '删除时间',
    `expire_time` DATETIME NOT NULL COMMENT '过期时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_expire` (`user_id`, `expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回收站表';
