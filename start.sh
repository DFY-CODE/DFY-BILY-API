#!/bin/bash

APP_NAME="one-dfy-bily-api-0.0.1-SNAPSHOT.jar"
LOG_FILE="app.log"
PID_FILE="app.pid"
PORT=443
PROFILE="prod"

# 실행 중인 프로세스 확인
if [ -f "$PID_FILE" ]; then
  PID=$(cat "$PID_FILE")
  if ps -p $PID > /dev/null; then
    echo "Application is already running (PID: $PID)"
    exit 1
  else
    echo "Removing stale PID file"
    rm -f "$PID_FILE"
  fi
fi

# 애플리케이션 실행
echo "Starting application with profile: $PROFILE..."
nohup java -jar "$APP_NAME" \
  --server.port=$PORT \
  --spring.profiles.active=$PROFILE > "$LOG_FILE" 2>&1 &

# PID 저장
echo $! > "$PID_FILE"
echo "Application started with PID $(cat "$PID_FILE")"
