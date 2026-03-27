#!/bin/bash

set -e

echo "===== 小程网盘部署脚本 ====="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# 检查Docker是否安装
check_docker() {
    if ! command -v docker &> /dev/null; then
        echo -e "${RED}错误: Docker 未安装${NC}"
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        echo -e "${RED}错误: Docker Compose 未安装${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}Docker 检查通过${NC}"
}

# 构建后端
build_backend() {
    echo -e "${YELLOW}正在构建后端...${NC}"
    cd "$(dirname "$0")"
    cd backend
    if [ ! -f "pom.xml" ]; then
        echo -e "${RED}错误: pom.xml 不存在${NC}"
        exit 1
    fi
    
    # 使用后端目录中的 maven wrapper 或系统 maven
    if [ -f "mvnw" ]; then
        chmod +x mvnw
        ./mvnw clean package -DskipTests -q
    else
        mvn clean package -DskipTests -q
    fi
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}后端构建成功${NC}"
    else
        echo -e "${RED}后端构建失败${NC}"
        exit 1
    fi
}

# 启动服务
start_services() {
    echo -e "${YELLOW}正在启动服务...${NC}"
    cd "$(dirname "$0")"
    docker-compose up -d
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}服务启动成功${NC}"
    else
        echo -e "${RED}服务启动失败${NC}"
        exit 1
    fi
}

# 等待服务健康
wait_healthy() {
    echo -e "${YELLOW}等待服务健康检查...${NC}"
    local max_attempts=60
    local attempt=0
    
    while [ $attempt -lt $max_attempts ]; do
        if docker-compose ps | grep -q "healthy"; then
            echo -e "${GREEN}所有服务已就绪${NC}"
            return 0
        fi
        attempt=$((attempt + 1))
        echo -e "等待服务启动... ($attempt/$max_attempts)"
        sleep 2
    done
    
    echo -e "${RED}服务启动超时${NC}"
    return 1
}

# 显示服务状态
show_status() {
    echo ""
    echo "===== 服务状态 ====="
    docker-compose ps
    echo ""
    echo "===== 访问地址 ====="
    echo "后端 API: http://localhost:8080"
    echo "API 文档: http://localhost:8080/doc.html"
    echo "MinIO 控制台: http://localhost:9001"
    echo "MySQL: localhost:3306"
    echo "Redis: localhost:6379"
}

# 主流程
main() {
    check_docker
    
    case "${1:-start}" in
        start)
            build_backend
            start_services
            wait_healthy
            show_status
            ;;
        stop)
            echo -e "${YELLOW}停止服务...${NC}"
            docker-compose down
            echo -e "${GREEN}服务已停止${NC}"
            ;;
        restart)
            $0 stop
            sleep 2
            $0 start
            ;;
        status)
            docker-compose ps
            ;;
        logs)
            docker-compose logs -f "${2:-backend}"
            ;;
        *)
            echo "用法: $0 {start|stop|restart|status|logs}"
            exit 1
            ;;
    esac
}

main "$@"
