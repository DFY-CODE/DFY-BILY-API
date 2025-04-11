#!/bin/bash

PID_FILE="app.pid"

# 실행 중인 프로세스 종료
if [ -f "$PID_FILE" ]; then
  PID=$(cat "$PID_FILE")
  if ps -p $PID > /dev/null; then
    echo "Stopping application (PID: $PID)..."
    kill $PID
    sleep 5
    if ps -p $PID > /dev/null; then
      echo "Force killing application (PID: $PID)..."
      kill -9 $PID
    fi
    rm -f "$PID_FILE"
    echo "Application stopped."
  else
    echo "No running process found for PID $PID. Removing stale PID file."
    rm -f "$PID_FILE"
  fi
else
  echo "No PID file found. Application may not be running."
fi
