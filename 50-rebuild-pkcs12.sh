# bash (예시) /etc/letsencrypt/renewal-hooks/deploy/50-rebuild-pkcs12.sh
set -e
DOMAIN=api.bily.at
openssl pkcs12 -export \
  -in /etc/letsencrypt/live/$DOMAIN/fullchain.pem \
  -inkey /etc/letsencrypt/live/$DOMAIN/privkey.pem \
  -out /home/ubuntu/bily/keystore.p12 \
  -name tomcat \
  -passout pass:dfydfy
chown ubuntu:ubuntu /home/ubuntu/bily/keystore.p12
chmod 640 /home/ubuntu/bily/keystore.p12
# 여기서 애플리케이션 안전 재시작 커맨드를 호출
# 예: systemctl restart <SERVICE_NAME> 또는 배포 스크립트 호출
