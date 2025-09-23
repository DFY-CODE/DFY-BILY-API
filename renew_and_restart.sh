#!/bin/bash

# Certbot이 갱신된 인증서를 저장하는 경로 (도메인에 따라 다름)
DOMAIN="api.bily.at"
CERT_PATH="/etc/letsencrypt/live/$DOMAIN"
P12_DEST="/home/ubuntu/bily/keystore.p12"

# Spring Boot keystore 비밀번호 (application.yaml에 설정된 비밀번호와 일치해야 합니다)
KEYSTORE_PASSWORD="dfydfy" # 비밀번호를 입력해주세요

echo "$(date): Certbot renewal started."

# Certbot을 실행하여 인증서 갱신 시도
# --force-renewal 은 테스트용이며, 실제 운영시에는 사용하지 않는 것이 좋습니다.
# sudo certbot renew --quiet --nginx (혹은 --webroot 등 설정에 맞게)
# 여기서는 --deploy-hook 에서 이 스크립트를 사용할 것이므로, 직접 renew를 호출하지 않습니다.

# Certbot이 성공적으로 인증서를 갱신했는지 확인
# Certbot의 --deploy-hook은 갱신 성공 시에만 이 스크립트를 실행합니다.
if [ -f "$CERT_PATH/fullchain.pem" ] && [ -f "$CERT_PATH/privkey.pem" ]; then
    echo "$(date): New certificates found. Converting to PKCS12..."

    # .pem -> .p12 변환 (별칭은 application.yaml의 key-alias와 동일하게 'tomcat'으로 설정)
    openssl pkcs12 -export \
      -out "$P12_DEST" \
      -inkey "$CERT_PATH/privkey.pem" \
      -in "$CERT_PATH/fullchain.pem" \
      -name tomcat \
      -password pass:"$KEYSTORE_PASSWORD" \
      -passin pass:"$KEYSTORE_PASSWORD" # private key 암호화에 사용된 비밀번호 (없다면 제거)

    if [ $? -eq 0 ]; then
        echo "$(date): PKCS12 conversion successful. Restarting Spring Boot application..."

        # Spring Boot 애플리케이션 재시작
        sudo systemctl restart your-springboot-service-name.service # 예: sudo systemctl restart bily-api.service

        if [ $? -eq 0 ]; then
            echo "$(date): Spring Boot application restarted successfully."
        else
            echo "$(date): Failed to restart Spring Boot application! Check logs."
        fi
    else
        echo "$(date): PKCS12 conversion failed! Check openssl command and paths."
    fi
else
    echo "$(date): No new certificates found or Certbot renewal failed. No action taken."
fi

echo "$(date): Certbot renewal script finished."
