#!/usr/bin/env bash
# 파일명: backup_mysql_full.sh
# 사용법:
#   기본   : ./backup_mysql_full.sh          → ./.env 사용
#   위치지정: ./backup_mysql_full.sh /path/to/other.env
#   또는    : ENV_FILE=/path/to/other.env ./backup_mysql_full.sh

set -euo pipefail

###############################################################################
# (.env 위치 설정 – 인자가 없으면 resources/.env 사용)
###############################################################################
if [[ $# -ge 1 ]]; then
  ENV_FILE="$1"
else
  # 스크립트가 있는 디렉터리를 기준으로 상대 경로 지정
  SCRIPT_DIR="$(cd -- "$(dirname "$0")" && pwd)"
  ENV_FILE="$SCRIPT_DIR/src/main/resources/.env"
fi

if [[ ! -f "$ENV_FILE" ]]; then
  echo "❌ .env 파일을 찾을 수 없습니다: $ENV_FILE" >&2
  exit 1
fi


###############################################################################
# (.env 로드)
###############################################################################
export $(grep -v '^#' "$ENV_FILE" | xargs)

###############################################################################
# (필수 변수 확인)
###############################################################################
: "${DB_USERNAME:?필수 환경변수(DB_USERNAME)가 비어 있습니다}"
: "${DB_PASSWORD:?필수 환경변수(DB_PASSWORD)가 비어 있습니다}"
: "${DB_HOST:?필수 환경변수(DB_HOST)가 비어 있습니다}"
: "${DB_NAME:?필수 환경변수(DB_NAME)가 비어 있습니다}"

###############################################################################
# (백업 실행)
###############################################################################
BACKUP_DIR="./backup"
FILE_NAME="${DB_NAME}_$(date +%F_%H%M%S).sql.gz"
mkdir -p "$BACKUP_DIR"

echo "⏳ 백업 시작  : $BACKUP_DIR/$FILE_NAME"
mysqldump \
  -u "$DB_USERNAME" \
  -p"$DB_PASSWORD" \
  -h "$DB_HOST" \
  --routines --triggers --single-transaction --quick \
  "$DB_NAME" | gzip > "$BACKUP_DIR/$FILE_NAME"

echo "✅ 백업 완료  : $BACKUP_DIR/$FILE_NAME (크기: $(du -h "$BACKUP_DIR/$FILE_NAME" | cut -f1))"