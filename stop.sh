#!/bin/bash

# 终止后端服务
if [ -f backend.pid ]; then
    echo "Stopping backend..."
    kill $(cat backend.pid)
    rm backend.pid
fi

# 终止前端服务
NODE_PID=$(lsof -i:3000 | grep 'node' | awk '{print $2}' | head -n 1)
if [ -n "$NODE_PID" ]; then
    echo "Stopping frontend..."
    kill $NODE_PID
fi

echo "All services stopped."