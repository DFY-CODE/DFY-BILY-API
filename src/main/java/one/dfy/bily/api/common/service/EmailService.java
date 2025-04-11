package one.dfy.bily.api.common.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean sendEmail(String to, String subject, String content) {
        try {
            // 1️⃣ 이메일 유효성 검사
            if (to == null || !isValidEmail(to)) {
                throw new AddressException("잘못된 이메일 형식: " + to);
            }

            // 2️⃣ 이메일 공백 제거
            to = to.trim();

            // 3️⃣ 이메일 메시지 생성 및 전송
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            helper.setFrom("kfgreen@naver.com");  // 네이버 계정과 동일해야 함

            mailSender.send(message);

            System.out.println("✅ 이메일 전송 성공: " + to);
            return true; // ✔️ 성공 시 true 반환
        } catch (MessagingException e) {
            System.err.println("❌ 이메일 전송 실패: " + e.getMessage());
            return false; // ❌ 실패 시 false 반환
        }
    }

    // 이메일 형식 검증 메서드
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email != null && email.matches(emailRegex);
    }
}


