# 小程网盘 - 技术设计规格说明书

## 1. 系统架构概述

### 1.1 整体架构

参照百度网盘的"客户端-服务器-存储集群"架构，小程网盘采用轻量化设计，分为前端层、后端服务层、存储层、安全层四大模块。

```
┌─────────────────────────────────────────────────────────┐
│                        客户端层                          │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │
│  │  移动端(iOS) │  │ 移动端(Android) │  │  PC端(Electron) │  │
│  └─────────────┘  └─────────────┘  └─────────────┘     │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                      后端服务层                           │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │
│  │  API Gateway │  │  业务服务   │  │  定时任务服务 │     │
│  └─────────────┘  └─────────────┘  └─────────────┘     │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                       存储层                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │
│  │  文件存储   │  │  数据库     │  │   Redis缓存  │     │
│  └─────────────┘  └─────────────┘  └─────────────┘     │
└─────────────────────────────────────────────────────────┘
```

### 1.2 技术选型

#### 前端技术栈

| 平台 | 技术方案 | 说明 |
|------|----------|------|
| 移动端 | Flutter | 跨平台开发，兼顾iOS和Android |
| PC端 | Electron | 桌面应用框架，支持拖拽操作 |
| 状态管理 | Riverpod/Provider | 轻量级状态管理 |
| 网络请求 | Dio | 统一网络请求库，支持拦截器 |
| 本地存储 | SharedPreferences/SQLite | 轻量本地持久化 |

#### 后端技术栈

| 组件 | 技术方案 | 说明 |
|------|----------|------|
| 后端框架 | Spring Boot 2.7.x | 生态完善，与MySQL/Redis/MyBatis集成良好 |
| 数据库 | MySQL 8.0 | 用户信息、文件metadata、分享记录 |
| 缓存 | Redis 6.x | 会话缓存、常用数据缓存 |
| 文件传输 | HTTP/HTTPS | 支持断点续传、分块上传 |
| 定时任务 | Quartz | 回收站清理、备份提醒 |
| ORM | MyBatis-Plus 3.5.x | 轻量级ORM框架 |

#### 存储技术栈

| 组件 | 技术方案 | 说明 |
|------|----------|------|
| 对象存储 | MinIO / 阿里云OSS | 自建或云服务 |
| 文件分片 | 分块存储 | 大文件分片存储 |
| 文件去重 | MD5/SHA256 | 文件唯一标识 |

#### 安全技术栈

| 组件 | 技术方案 | 说明 |
|------|----------|------|
| 传输加密 | HTTPS | TLS 1.3 |
| 存储加密 | AES-256 | 文件内容加密 |
| 密码加密 | BCrypt | 密码哈希 |
| 权限控制 | JWT | 接口鉴权 |

## 2. 前端设计

### 2.1 移动端架构

```
lib/
├── main.dart
├── app/
│   ├── app.dart
│   └── router.dart
├── core/
│   ├── constants/
│   ├── theme/
│   ├── utils/
│   └── network/
├── data/
│   ├── models/
│   ├── repositories/
│   └── providers/
├── features/
│   ├── home/
│   │   ├── file_list/
│   │   ├── search/
│   │   └── sort/
│   ├── backup/
│   │   ├── photo_backup/
│   │   └── video_backup/
│   ├── share/
│   │   ├── create_share/
│   │   └── share_list/
│   └── profile/
│       ├── settings/
│       └── security/
└── shared/
    ├── widgets/
    └── dialogs/
```

### 2.2 移动端导航结构

**底部导航（4个Tab）**

| Tab | 图标 | 名称 | 说明 |
|-----|------|------|------|
| 1 | Home | 首页 | 文件列表、搜索、排序 |
| 2 | Backup | 备份 | 相册备份、视频备份、备份设置 |
| 3 | Share | 分享 | 分享记录、收到的分享 |
| 4 | Profile | 我的 | 个人信息、设置、会员中心 |

### 2.3 PC端布局

**左侧导航**

| 菜单项 | 说明 |
|--------|------|
| 文件列表 | 主文件管理界面 |
| 收藏 | 收藏文件快速访问 |
| 回收站 | 删除文件管理 |
| 备份 | PC文件夹同步设置 |
| 分享记录 | 分享历史查看 |

### 2.4 主题色彩规范

| 颜色类型 | 色值 | 说明 |
|----------|------|------|
| 主色 | #1E88E5 | 蓝色系，贴合云存储、安全属性 |
| 主色深 | #1565C0 | 主色深色，用于hover等状态 |
| 主色浅 | #64B5F6 | 主色浅色，用于背景等 |
| 辅助色 | #F5F5F5 | 浅灰色，用于背景 |
| 文字主色 | #212121 | 深灰色，主要文字 |
| 文字辅助色 | #757575 | 中灰色，次要文字 |
| 错误色 | #E53935 | 红色，错误提示 |
| 成功色 | #43A047 | 绿色，成功提示 |
| 警告色 | #FB8C00 | 橙色，警告提示 |

### 2.5 组件设计

#### 按钮组件

| 类型 | 样式 | 使用场景 |
|------|------|----------|
| 主按钮 | 蓝色填充，白色文字 | 主要操作，如上传、分享 |
| 次按钮 | 蓝色边框，蓝色文字 | 次要操作，如取消 |
| 文字按钮 | 无边框，蓝色文字 | 低强调操作 |

#### 文件列表项

```
┌─────────────────────────────────────────────────────────┐
│ [缩略图] 文件名.jpg                    [更多▼]          │
│        修改时间  |  大小                               │
└─────────────────────────────────────────────────────────┘
```

#### 上传/下载进度条

```
┌─────────────────────────────────────────────────────────┐
│ 文件名.jpg                                    75%  ████░░│
│ 已上传: 7.5MB / 10MB                    [暂停] [取消]  │
└─────────────────────────────────────────────────────────┘
```

## 3. 后端设计

### 3.1 服务模块划分

```
com.xiaocheng.netdisk/
├── netdisk-api/           # API层
│   ├── controller/       # 控制器
│   ├── dto/              # 数据传输对象
│   └── validator/        # 参数校验
├── netdisk-service/      # 业务逻辑层
│   ├── file/             # 文件管理服务
│   ├── share/            # 分享服务
│   ├── backup/           # 备份服务
│   ├── user/             # 用户服务
│   └── security/         # 安全服务
├── netdisk-dal/          # 数据访问层
│   ├── entity/           # 实体类
│   ├── mapper/           # MyBatis Mapper
│   └── repository/       # JPA Repository
├── netdisk-common/       # 公共模块
│   ├── exception/        # 异常定义
│   ├── enums/            # 枚举类
│   └── utils/            # 工具类
└── netdisk-schedule/     # 定时任务模块
```

### 3.2 核心接口设计

#### 用户接口

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/user/register | POST | 用户注册 |
| /api/user/login | POST | 用户登录 |
| /api/user/sendCode | POST | 发送验证码 |
| /api/user/logout | POST | 退出登录 |
| /api/user/info | GET | 获取用户信息 |
| /api/user/updatePassword | PUT | 修改密码 |
| /api/user/updateInfo | PUT | 更新用户信息 |

#### 设备管理接口

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/device/list | GET | 获取设备列表 |
| /api/device/logout | POST | 退出指定设备 |
| /api/device/verify | POST | 验证设备安全 |
| /api/device/update | PUT | 更新设备信息 |

#### 文件管理接口

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/file/list | GET | 获取文件列表 |
| /api/file/upload/init | POST | 初始化分片上传 |
| /api/file/upload/chunks | GET | 获取已上传分片列表 |
| /api/file/upload/chunk | POST | 上传单个分片 |
| /api/file/upload/merge | POST | 合并分片 |
| /api/file/upload/cancel | DELETE | 取消上传任务 |
| /api/file/download | GET | 下载文件 |
| /api/file/delete | DELETE | 删除文件 |
| /api/file/move | PUT | 移动文件 |
| /api/file/copy | POST | 复制文件 |
| /api/file/rename | PUT | 重命名文件 |
| /api/file/createFolder | POST | 创建文件夹 |
| /api/file/search | GET | 搜索文件 |
| /api/file/favorite | POST | 收藏文件 |
| /api/file/unfavorite | POST | 取消收藏 |
| /api/file/recent | GET | 最近文件 |
| /api/file/detail | GET | 文件详情 |
| /api/file/thumbnail | GET | 获取缩略图 |

#### 分享接口

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/share/create | POST | 创建分享 |
| /api/share/cancel | DELETE | 取消分享 |
| /api/share/list | GET | 分享列表 |
| /api/share/detail | GET | 分享详情 |
| /api/share/save | POST | 保存分享文件 |
| /api/share/validate | GET | 验证分享密码 |

#### 备份接口

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/backup/photo | POST | 照片备份 |
| /api/backup/video | POST | 视频备份 |
| /api/backup/syncFolder | POST | 同步文件夹设置 |
| /api/backup/history | GET | 备份历史 |
| /api/backup/restore | POST | 恢复备份 |
| /api/backup/setting | GET | 获取备份设置 |
| /api/backup/setting | PUT | 更新备份设置 |

#### 回收站接口

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/recycle/list | GET | 回收站列表 |
| /api/recycle/restore | POST | 恢复文件 |
| /api/recycle/delete | DELETE | 彻底删除 |
| /api/recycle/empty | DELETE | 清空回收站 |

#### 好友管理接口

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/friend/list | GET | 好友列表 |
| /api/friend/add | POST | 添加好友 |
| /api/friend/accept | POST | 接受好友请求 |
| /api/friend/reject | POST | 拒绝好友请求 |
| /api/friend/delete | DELETE | 删除好友 |
| /api/friend/search | GET | 搜索用户 |

### 3.3 接口参数规范

#### 请求头

| 参数 | 说明 | 必填 |
|------|------|------|
| Authorization | Bearer Token | 是 |
| Content-Type | application/json | 是 |
| Request-ID | 请求唯一标识 | 否 |

#### 响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1701234567890
}
```

#### 分页参数

| 参数 | 说明 | 默认值 |
|------|------|--------|
| page | 页码 | 1 |
| pageSize | 每页条数 | 20 |
| sort | 排序字段 | createTime |
| order | 排序方向 | desc |

#### JWT Token规范

**AccessToken**
| 字段 | 说明 |
|------|------|
| userId | 用户ID |
| username | 用户名 |
| deviceId | 设备ID |
| exp | 过期时间（2小时） |
| iat | 签发时间 |

**RefreshToken**
| 字段 | 说明 |
|------|------|
| userId | 用户ID |
| tokenVersion | Token版本号 |
| deviceId | 设备ID |
| exp | 过期时间（7天） |

**Token刷新策略**
- 每次访问接口时验证AccessToken
- AccessToken过期时，使用RefreshToken刷新
- 刷新时检查tokenVersion是否匹配
- 远程退出登录时递增tokenVersion，使旧Token失效

#### 验证码防刷机制

| 限制类型 | 限制规则 |
|----------|----------|
| 同一IP | 1分钟1次，1小时5次，1天10次 |
| 同一手机号 | 1分钟1次，1天10次 |
| 错误次数 | 超限锁定30分钟 |
| 验证码有效期 | 5分钟 |
| 验证码格式 | 6位数字 |

## 4. 数据库设计

### 4.1 ER图概述

```
用户表 (user)
    │
    ├──文件表 (file)              1:N
    │     ├──文件分片表 (file_chunk)
    │     ├──文件标签表 (file_tag)
    │     └──文件版本表 (file_version)  1:N
    │
    ├──分享表 (share)             1:N
    │     └──分享访问记录表 (share_access)
    │
    ├──备份任务表 (backup_task)   1:N
    │     └──备份历史表 (backup_history)
    │
    ├──回收站表 (recycle)         1:N
    │
    ├──设备表 (device)            1:N
    │
    ├──好友关系表 (friend)        1:N
    │
    └──分片上传任务表 (upload_task)  1:N
```

### 4.2 表结构设计

#### 用户表 (user)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| username | VARCHAR(50) | 用户名 |
| password | VARCHAR(255) | 密码（BCrypt加密） |
| phone | VARCHAR(20) | 手机号 |
| email | VARCHAR(100) | 邮箱 |
| nickname | VARCHAR(50) | 昵称 |
| avatar | VARCHAR(255) | 头像URL |
| total_capacity | BIGINT | 总容量（字节） |
| used_capacity | BIGINT | 已用容量 |
| member_type | TINYINT | 会员类型 0-普通 1-会员 |
| member_expire_time | DATETIME | 会员过期时间 |
| status | TINYINT | 状态 0-正常 1-禁用 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

#### 文件表 (file)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| parent_id | BIGINT | 父文件夹ID |
| file_name | VARCHAR(255) | 文件名 |
| file_path | VARCHAR(1000) | 文件路径 |
| file_size | BIGINT | 文件大小 |
| file_type | VARCHAR(50) | 文件类型 |
| mime_type | VARCHAR(100) | MIME类型 |
| is_folder | TINYINT | 是否文件夹 0-文件 1-文件夹 |
| file_hash | VARCHAR(64) | 文件哈希（用于去重） |
| storage_path | VARCHAR(500) | 存储路径 |
| thumbnail | VARCHAR(255) | 缩略图URL |
| is_favorite | TINYINT | 是否收藏 |
| is_deleted | TINYINT | 是否删除 |
| delete_time | DATETIME | 删除时间 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

#### 分享表 (share)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| share_code | VARCHAR(32) | 分享码 |
| user_id | BIGINT | 分享用户ID |
| file_ids | VARCHAR(500) | 分享文件ID列表 |
| share_type | TINYINT | 分享类型 1-公开 2-密码 3-指定好友 |
| password | VARCHAR(100) | 分享密码 |
| expire_time | DATETIME | 过期时间 |
| view_count | INT | 浏览次数 |
| download_count | INT | 下载次数 |
| status | TINYINT | 状态 0-有效 1-已取消 |
| create_time | DATETIME | 创建时间 |

#### 分享访问记录表 (share_access)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| share_id | BIGINT | 分享ID |
| access_user_id | BIGINT | 访问用户ID |
| access_type | TINYINT | 访问类型 1-查看 2-下载 |
| access_time | DATETIME | 访问时间 |
| ip_address | VARCHAR(50) | IP地址 |
| device_info | VARCHAR(200) | 设备信息 |

#### 备份任务表 (backup_task)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| task_type | TINYINT | 任务类型 1-相册 2-视频 3-文件夹 |
| source_path | VARCHAR(500) | 源路径 |
| target_path | VARCHAR(500) | 目标路径 |
| backup_mode | TINYINT | 备份模式 1-仅WiFi 2-所有网络 |
| quality_mode | TINYINT | 质量模式 1-原图 2-压缩 |
| status | TINYINT | 状态 0-未启动 1-运行中 2-暂停 3-停止 |
| last_backup_time | DATETIME | 上次备份时间 |
| create_time | DATETIME | 创建时间 |

#### 回收站表 (recycle)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| file_id | BIGINT | 原始文件ID |
| file_name | VARCHAR(255) | 文件名 |
| file_path | VARCHAR(1000) | 文件路径 |
| file_size | BIGINT | 文件大小 |
| file_type | VARCHAR(50) | 文件类型 |
| delete_time | DATETIME | 删除时间 |
| expire_time | DATETIME | 过期时间（删除时间+30天） |

#### 设备表 (device)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| device_id | VARCHAR(64) | 设备唯一标识 |
| device_name | VARCHAR(100) | 设备名称 |
| device_type | TINYINT | 设备类型 1-iOS 2-Android 3-PC Web |
| last_login_time | DATETIME | 最后登录时间 |
| last_ip | VARCHAR(50) | 最后登录IP |
| last_location | VARCHAR(200) | 最后登录位置 |
| status | TINYINT | 状态 0-正常 1-禁用 |
| create_time | DATETIME | 创建时间 |

#### 文件版本表 (file_version)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| file_id | BIGINT | 文件ID |
| version_id | VARCHAR(32) | 版本标识 |
| file_hash | VARCHAR(64) | 文件哈希 |
| file_size | BIGINT | 文件大小 |
| storage_path | VARCHAR(500) | 存储路径 |
| source | TINYINT | 来源 1-相册备份 2-视频备份 3-手动上传 |
| create_time | DATETIME | 创建时间 |

#### 好友关系表 (friend)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| friend_id | BIGINT | 好友用户ID |
| status | TINYINT | 状态 0-待确认 1-已添加 2-已拒绝 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

#### 分片上传任务表 (upload_task)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| upload_id | VARCHAR(64) | 上传任务ID |
| user_id | BIGINT | 用户ID |
| file_name | VARCHAR(255) | 文件名 |
| file_size | BIGINT | 文件大小 |
| file_hash | VARCHAR(64) | 文件哈希 |
| parent_id | BIGINT | 父文件夹ID |
| chunk_count | INT | 分片总数 |
| uploaded_chunks | VARCHAR(500) | 已上传分片列表，JSON格式 |
| status | TINYINT | 状态 0-进行中 1-已完成 2-已取消 |
| create_time | DATETIME | 创建时间 |
| expire_time | DATETIME | 过期时间 |

#### 验证码表 (verify_code)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| target | VARCHAR(50) | 验证码目标（手机号/邮箱） |
| code | VARCHAR(10) | 验证码 |
| type | TINYINT | 类型 1-登录 2-注册 3-修改密码 |
| ip_address | VARCHAR(50) | 请求IP |
| error_count | INT | 错误次数 |
| expire_time | DATETIME | 过期时间 |
| create_time | DATETIME | 创建时间 |

### 4.3 索引设计

| 表名 | 索引字段 | 索引类型 | 说明 |
|------|----------|----------|------|
| user | phone | UNIQUE | 手机号唯一 |
| user | username | UNIQUE | 用户名唯一 |
| file | user_id, parent_id | INDEX | 用户文件列表 |
| file | user_id, is_deleted | INDEX | 用户回收站 |
| file | file_hash | INDEX | 文件去重 |
| share | share_code | UNIQUE | 分享码唯一 |
| share | user_id | INDEX | 用户分享列表 |
| recycle | user_id, expire_time | INDEX | 回收站清理 |
| device | user_id | INDEX | 用户设备列表 |
| device | device_id | UNIQUE | 设备唯一标识 |
| file_version | file_id | INDEX | 文件版本历史 |
| upload_task | upload_id | UNIQUE | 上传任务唯一 |
| upload_task | user_id, status | INDEX | 用户进行中上传 |
| verify_code | target, type | INDEX | 验证码查询 |
| verify_code | expire_time | INDEX | 验证码清理 |

## 5. 安全设计

### 5.1 传输安全

- 全链路HTTPS加密
- TLS 1.3协议
- 证书双向认证（可选）

### 5.2 存储安全

- 用户密码BCrypt加密
- 文件内容AES-256加密
- 文件分片加密存储

### 5.3 接口安全

- JWT Token鉴权
- 接口权限控制
- 请求频率限制
- 防SQL注入
- 防XSS攻击
- 防CSRF攻击

### 5.4 安全策略

| 策略 | 说明 |
|------|------|
| 登录失败锁定 | 连续5次失败锁定30分钟 |
| Token过期 | AccessToken 2小时，RefreshToken 7天 |
| Token版本控制 | 远程退出通过递增tokenVersion实现 |
| 异常登录提醒 | 新设备登录推送通知 |
| 文件权限控制 | 密码保护文件单独验证 |
| 敏感操作验证 | 修改密码、删除文件等需二次验证 |

### 5.5 传输层安全

| 安全措施 | 说明 |
|----------|------|
| HTTPS强制 | 所有HTTP请求重定向至HTTPS |
| TLS版本 | TLS 1.3（兼容1.2） |
| 证书配置 | 权威CA签发证书 |
| HSTS | 启用HTTP严格传输安全 |
| 安全请求头 | X-Frame-Options, CSP, X-XSS-Protection |

## 6. 文件存储设计

### 6.1 存储结构

```
/storage/
├── temp/                  # 临时文件
├── chunk/                # 分片文件
├── file/                 # 正式文件
│   └── {year}/{month}/{day}/{user_id}/{file_hash}/
├── thumbnail/            # 缩略图
│   └── {year}/{month}/{day}/{file_id}/
└── backup/               # 备份文件
    └── {user_id}/{task_type}/
```

### 6.2 分片上传流程

```
1. 客户端计算文件Hash（MD5+SHA256）
2. 客户端请求 /upload/init
   - 传入：fileName, fileSize, fileHash, parentId
   - 返回：uploadId, chunkSize(5MB), chunkCount
3. 客户端检查是否已存在相同文件（通过fileHash）
   - 如存在，直接关联现有文件，无需上传
4. 客户端查询 /upload/chunks 获取已上传分片
5. 客户端跳过已上传分片，并行上传剩余分片
6. 服务端验证分片Hash，存储至chunk目录
7. 所有分片上传完成后，客户端请求 /upload/merge
8. 服务端合并分片，计算最终文件Hash
9. 进行去重检查：
   - 如已存在，关联现有文件
   - 如不存在，保存新文件，清理分片
10. 清理超时未完成的upload_task（24小时）
```

### 6.3 断点续传机制

**状态管理**
- 服务端记录每个upload_task的uploaded_chunks（JSON数组）
- 客户端记录本地已上传分片索引

**恢复流程**
```
1. 客户端查询 /upload/chunks?uploadId=xxx
2. 服务端返回已上传分片列表 [0, 1, 3]
3. 客户端从分片2继续上传
4. 后续流程与正常上传一致
```

**暂停/恢复**
- 暂停：客户端停止上传，状态已保存
- 恢复：重新查询已上传分片，继续上传

### 6.4 文件去重

**去重策略**
- 计算文件Hash：MD5+SHA256混合
- 存储时检查Hash是否存在
- 如存在，关联现有文件（不同用户、不同目录可共享同一物理文件）
- 引用计数：记录有多少文件引用同一物理存储
- 引用计数为0时，可执行清理（根据备份策略保留版本）

## 7. 备份同步设计

### 7.1 相册备份流程

```
1. 用户开启相册备份，设置备份模式（WiFi/所有网络、原图/压缩）
2. 系统申请相册读取权限，扫描相册文件
3. 对比已备份文件（基于file_hash），排除相同文件
4. 根据设置进行图片压缩（如需要）
5. 执行分片上传，实时更新备份进度
6. 上传完成后记录file_version
7. 备份完成推送通知
8. 支持暂停/继续备份
```

### 7.2 PC文件夹同步流程

```
1. 用户选择本地同步文件夹
2. 系统计算本地文件Hash列表
3. 首次全量同步：上传所有文件
4. 启动文件监听（文件系统事件）
5. 本地文件变化时：
   - 创建/修改：增量上传
   - 删除：移动到回收站
6. 定时检查云端变化（每5分钟）
7. 云端变化时同步至本地
8. 冲突处理策略：
   - 提示用户选择：保留本地/保留云端/保留两者
```

### 7.3 备份冲突处理

| 冲突类型 | 处理策略 |
|----------|----------|
| 同一文件两端同时修改 | 保留两者，以时间戳区分 |
| 本地删除，云端修改 | 保留云端版本 |
| 云端删除，本地修改 | 保留本地版本至回收站 |
| 批量冲突 | 批量提示用户选择 |

## 8. 定时任务设计

### 8.1 任务列表

| 任务名称 | 执行时间 | 说明 |
|----------|----------|------|
| recycle-clean | 每天凌晨2点 | 清理过期回收站文件 |
| backup-remind | 每天上午10点 | 备份提醒 |
| capacity-stat | 每天凌晨3点 | 容量统计 |
| online-user-stat | 每小时 | 在线用户统计 |
| file-dedup | 每周日凌晨 | 文件去重检查 |
| log-clean | 每周日凌晨 | 清理30天前日志 |

## 9. 日志设计

### 9.1 日志类型

| 日志类型 | 说明 | 保留时间 |
|----------|------|----------|
| 操作日志 | 用户文件操作记录 | 180天 |
| 访问日志 | 接口访问记录 | 90天 |
| 错误日志 | 系统异常记录 | 180天 |
| 安全日志 | 登录/权限记录 | 180天 |
| 传输日志 | 上传下载记录 | 30天 |

### 9.2 日志格式

```json
{
  "timestamp": "2024-01-01 12:00:00",
  "level": "INFO",
  "type": "USER_OPERATION",
  "userId": 12345,
  "action": "FILE_UPLOAD",
  "fileId": 67890,
  "fileName": "test.jpg",
  "fileSize": 1024000,
  "ip": "192.168.1.1",
  "device": "iOS/16.0",
  "duration": 5000,
  "status": "SUCCESS",
  "message": ""
}
```

## 10. 部署架构

### 10.1 开发环境

```
开发环境配置：
- 后端：Spring Boot embedded Tomcat
- 数据库：MySQL 8.0 (Docker)
- 缓存：Redis 6.x (Docker)
- 存储：MinIO (Docker)
```

### 10.2 生产环境

```
生产环境配置：
- 后端：K8s Deployment
- 数据库：MySQL 8.0 Cluster
- 缓存：Redis Cluster
- 存储：阿里云OSS
- CDN：阿里云CDN
- 负载均衡：Nginx/K8s Ingress
```

### 10.3 高可用设计

| 组件 | 高可用方案 |
|------|------------|
| API服务 | K8s HPA自动扩缩容 |
| 数据库 | MySQL主从复制 |
| 缓存 | Redis Sentinel/Cluster |
| 存储 | 多AZ冗余存储 |
| 文件 | 多节点备份 |

## 11. 性能指标

| 指标 | 目标值 |
|------|--------|
| API响应时间(P99) | < 200ms |
| 文件上传速度 | 达到网络带宽上限 |
| 文件下载速度 | 达到网络带宽上限 |
| 同时在线用户 | 1000+ |
| 文件存储可靠性 | 99.999% |
| 服务可用性 | 99.9% |

## 12. 附录

### 12.1 第三方依赖

| 依赖 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 2.7.x | 后端框架 |
| MyBatis-Plus | 3.5.x | ORM框架 |
| Redis | 6.x | 缓存 |
| MinIO | latest | 对象存储 |
| JWT | 0.9.x | Token认证 |
| Lombok | latest | 简化代码 |

### 12.2 开发规范

- 代码风格：Google Java Style Guide
- 分支管理：Git Flow
- 代码审查：PR Review
- 测试覆盖：核心业务 > 80%
