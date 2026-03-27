# 小程网盘 - 云端部署指南

## 概述

本文档提供小程网盘完整云端部署的详细步骤。

## 系统要求

- Docker 20.10+
- Docker Compose 1.29+
- MySQL 8.0
- Redis 7.x
- MinIO (对象存储)
- JDK 17+

## 快速部署

### 1. 准备环境

```bash
# 克隆代码
git clone https://github.com/ERICK886/XiaochengCloud.git
cd XiaochengCloud

# 或在已有代码目录下
git pull origin master
```

### 2. 一键部署

```bash
chmod +x deploy.sh
./deploy.sh start
```

## 手动部署

### 步骤 1: 启动基础服务 (MySQL, Redis, MinIO)

```bash
# 启动数据库服务
docker network create xiaocheng-net

# MySQL
docker run -d \
  --name xiaocheng-mysql \
  --network xiaocheng-net \
  -e MYSQL_ROOT_PASSWORD=root123456 \
  -e MYSQL_DATABASE=xiaocheng_netdisk \
  -p 3306:3306 \
  -v /data/xiaocheng/mysql:/var/lib/mysql \
  -v $(pwd)/init.sql:/docker-entrypoint-initdb.d/init.sql \
  mysql:8.0 \
  --default-authentication-plugin=mysql_native_password

# Redis
docker run -d \
  --name xiaocheng-redis \
  --network xiaocheng-net \
  -p 6379:6379 \
  -v /data/xiaocheng/redis:/data \
  redis:7-alpine

# MinIO
docker run -d \
  --name xiaocheng-minio \
  --network xiaocheng-net \
  -p 9000:9000 \
  -p 9001:9001 \
  -e MINIO_ROOT_USER=minioadmin \
  -e MINIO_ROOT_PASSWORD=minioadmin \
  -v /data/xiaocheng/minio:/data \
  minio/minio server /data --console-address ":9001"
```

### 步骤 2: 构建后端

```bash
cd backend

# 使用 Maven 构建
mvn clean package -DskipTests

# 或使用 Docker 构建
docker build -t xiaocheng-backend .
```

### 步骤 3: 运行后端

```bash
docker run -d \
  --name xiaocheng-backend \
  --network xiaocheng-net \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://xiaocheng-mysql:3306/xiaocheng_netdisk \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=root123456 \
  -e SPRING_REDIS_HOST=xiaocheng-redis \
  -e SPRING_REDIS_PORT=6379 \
  -e MINIO_ENDPOINT=http://xiaocheng-minio:9000 \
  xiaocheng-backend
```

## 使用 Docker Compose 部署 (推荐)

### 完整服务编排

```yaml
# docker-compose.yml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: xiaocheng-mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: root123456
      MYSQL_DATABASE: xiaocheng_netdisk
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]

  redis:
    image: redis:7-alpine
    container_name: xiaocheng-redis
    restart: always
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data

  minio:
    image: minio/minio:latest
    container_name: xiaocheng-minio
    restart: always
    environment:
      MINIO_ROOT_USER: minioadmin
      MINIO_ROOT_PASSWORD: minioadmin
    ports:
      - "9000:9000"
      - "9001:9001"
    volumes:
      - minio_data:/data
    command: server /data --console-address ":9001"

  backend:
    build: ./backend
    container_name: xiaocheng-backend
    restart: always
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/xiaocheng_netdisk
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root123456
      SPRING_REDIS_HOST: redis
      SPRING_REDIS_PORT: 6379
      MINIO_ENDPOINT: http://minio:9000
    depends_on:
      mysql:
        condition: service_healthy
      redis:
        condition: service_healthy
      minio:
        condition: service_healthy

volumes:
  mysql_data:
  redis_data:
  minio_data:
```

### 启动命令

```bash
# 构建并启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f backend
```

## 服务访问

部署完成后，可访问以下地址：

| 服务 | 地址 | 说明 |
|------|------|------|
| 后端 API | http://localhost:8080 | Spring Boot API |
| API 文档 | http://localhost:8080/doc.html | Knife4j 文档 |
| MinIO 控制台 | http://localhost:9001 | 对象存储管理 |
| MinIO API | http://localhost:9000 | S3 兼容 API |
| MySQL | localhost:3306 | 数据库 |

## 数据库初始化

首次启动时，`init.sql` 会自动创建所有表结构和测试用户：

- 测试用户: `admin`
- 测试密码: `123456`

## 运维命令

```bash
# 启动服务
./deploy.sh start

# 停止服务
./deploy.sh stop

# 重启服务
./deploy.sh restart

# 查看状态
./deploy.sh status

# 查看日志
./deploy.sh logs backend
```

## 移动端开发

```bash
cd mobile

# 安装依赖
flutter pub get

# 运行开发版本
flutter run

# 构建 Android APK
flutter build apk

# 构建 iOS
flutter build ios
```

## PC端开发

```bash
cd desktop

# 安装依赖
npm install

# 开发模式运行
npm start

# 构建可执行文件
npm run build
```

## 常见问题

### 1. Docker 镜像拉取失败

如果遇到镜像拉取问题，可配置国内镜像加速器：

```bash
# /etc/docker/daemon.json
{
  "registry-mirrors": [
    "https://mirror.ccs.tencentyun.com",
    "https://docker.mirrors.ustc.edu.cn"
  ]
}
```

### 2. 数据库连接失败

检查 MySQL 是否完全启动：
```bash
docker logs xiaocheng-mysql
docker exec -it xiaocheng-mysql mysql -uroot -proot123456
```

### 3. 端口冲突

如果端口被占用，修改 `docker-compose.yml` 中的端口映射。

## 安全建议

1. **修改默认密码**: 部署前修改 `init.sql` 中的测试用户密码
2. **配置 HTTPS**: 生产环境建议配置反向代理启用 HTTPS
3. **限制数据库访问**: 配置防火墙规则限制 MySQL 端口访问
4. **配置备份**: 定期备份 MySQL 数据和 MinIO 存储
