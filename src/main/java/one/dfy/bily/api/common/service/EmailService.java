package one.dfy.bily.api.common.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

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

    public boolean sendResetPasswordEmail(String to, String resetLink) {
        try {
            if (to == null || !isValidEmail(to)) {
                throw new AddressException("잘못된 이메일 형식: " + to);
            }

            to = to.trim();

            String subject = "비밀번호 재설정 안내";
            String content = buildResetPasswordEmail(resetLink);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            helper.setFrom("kfgreen@naver.com"); // 실제 이메일 주소와 일치해야 함

            mailSender.send(message);
            System.out.println("✅ 이메일 전송 성공: " + to);
            return true;

        } catch (MessagingException e) {
            System.err.println("❌ 이메일 전송 실패: " + e.getMessage());
            return false;
        }
    }

    public String buildResetPasswordEmail(String resetLink) {
        Context context = new Context();
        context.setVariable("resetLink", resetLink);
        return templateEngine.process("reset-password", context);
    }

    // 이메일 형식 검증 메서드
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email != null && email.matches(emailRegex);
    }
}


