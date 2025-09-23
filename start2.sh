#!/bin/bash

APP_NAME="one-dfy-bily-api-0.0.1-SNAPSHOT.jar"
# LOG_FILE="app.log" # Spring Boot 내부에서 로그 파일을 관리하도록 변경합니다.
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

# 로그 파일 정리: 수정된 지 1일이 지난 .log 파일을 삭제합니다.
# 이 명령어는 디스크 용량 문제를 방지하는 데 도움이 됩니다.
# Spring Boot 내부 로깅 설정 후에도 필요한 경우에만 유지하세요.
# 만약 Spring Boot가 `/var/log/your-app`과 같은 특정 디렉토리에 로그를 생성한다면
# `find /var/log/your-app -type f -name "*.log" -mtime +1 -delete` 와 같이 경로를 명시하는 것이 좋습니다.
find . -type f -name "*.log" -mtime +1 -delete

# 애플리케이션 실행
echo "Starting application with profile: $PROFILE..."
# 이제 nohup은 콘솔 출력을 `/dev/null`로 보내고, Spring Boot가 직접 로그 파일을 관리하게 합니다.
# JVM 메모리 설정을 조금 더 여유롭게 조정해 볼 수 있습니다. (예: -Xms512m -Xmx1024m)
# Lightsail 인스턴스 사양에 맞춰 조절해 주세요.
nohup java -Xms256m -Xmx512m -jar "$APP_NAME" \
  --server.port=$PORT \
  --spring.profiles.active=$PROFILE > /dev/null 2>&1 &

#  --spring.profiles.active=$PROFILE > "$LOG_FILE" 2>&1 &  # Spring Boot 로그 파일 서버에서 확인할수 있도록

# PID 저장
echo $! > "$PID_FILE"
echo "Application started with PID $(cat "$PID_FILE")"
echo "Check Spring Boot internal log files for details (e.g., /var/log/one-dfy-bily-api/application.log)"