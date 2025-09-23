#!/bin/bash

APP_NAME="one-dfy-bily-api-0.0.1-SNAPSHOT.jar"
PID_FILE="app.pid"
PORT=443
PROFILE="prod"
LOG_DIR="/home/ubuntu/bily"
OUT_FILE="${LOG_DIR}/app.log"

# 로그 디렉토리와 파일 생성 및 권한 설정
echo "Setting up log directory and permissions..."
sudo mkdir -p "$LOG_DIR"
sudo chown -R `whoami`:`whoami` "$LOG_DIR"

# 실행 중인 프로세스 확인 (PID 파일 기반)
if [ -f "$PID_FILE" ]; then
  PID=$(cat "$PID_FILE")
  if ps -p $PID > /dev/null; then
    echo "Application is already running (PID: $PID)"
    exit 1
  else
    echo "Removing stale PID file: $PID_FILE"
    rm -f "$PID_FILE"
  fi
fi

# 불필요한 로그 파일 정리 스크립트 삭제 (Logback 롤링 정책 사용)
# find . -type f -name "*.log" -mtime +1 -delete

# 애플리케이션 실행
echo "Starting application with profile: $PROFILE..."
# nohup 출력을 특정 파일로 리디렉션하여 시작 오류 확인 가능하게 함
# JVM 메모리 설정은 필요에 따라 조절 (예: Lightsail 1GB RAM 인스턴스: -Xms512m -Xmx768m)
nohup java -Xms256m -Xmx512m -XX:+UseStringDeduplication -jar "$APP_NAME" \
  --server.port=$PORT \
  --spring.profiles.active=$PROFILE > "$OUT_FILE" 2>&1 &

# PID 저장
echo $! > "$PID_FILE"
echo "Application started with PID $(cat "$PID_FILE")"
echo "Check startup log at: $OUT_FILE"
echo "Check application log at: ${LOG_DIR}/application.log"