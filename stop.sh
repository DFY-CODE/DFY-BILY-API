#!/bin/bash

PID_FILE="app.pid"

# 실행 중인 프로세스 종료
if [ -f "$PID_FILE" ]; then
  PID=$(cat "$PID_FILE")
  if ps -p "$PID" > /dev/null 2>&1; then
    echo "Stopping application (PID: $PID)..."
    kill "$PID"
    sleep 5
    
    # 프로세스가 종료되지 않은 경우 강제 종료 시도
    if ps -p "$PID" > /dev/null 2>&1; then
      echo "Force killing application (PID: $PID)..."
      kill -9 "$PID"
      
      # 강제 종료 실패 여부 확인
      if ps -p "$PID" > /dev/null 2>&1; then
        echo "Failed to kill application process (PID: $PID). Please check manually."
        exit 1
      fi
    fi

    # PID 파일 제거
    rm -f "$PID_FILE"
    echo "Application stopped successfully."
  else
    # PID 파일이 있으나 프로세스가 없는 경우
    echo "No running process found for PID $PID. Removing stale PID file."
    rm -f "$PID_FILE"
  fi
else
  # PID 파일이 없는 경우
  echo "No PID file found. Application may not be running."
fi