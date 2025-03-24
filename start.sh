#!/bin/bash
set -e  # 任何命令失败时，脚本自动停止
# 进入后端目录
cd message-board-backend

# 安装 Maven 依赖并启动 Spring Boot 服务
echo "Starting backend..."
mvn clean compile
mvn spring-boot:run &
echo $! > ../backend.pid

# 返回项目根目录
cd ..

# 进入前端目录
cd message-board-frontend

# 安装 npm 依赖并启动 React 开发服务器
echo "Starting frontend..."
npm install
npm start &

# 返回项目根目录
cd ..
echo "All services started."