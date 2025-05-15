package one.dfy.bily.api.auth.service;

import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.auth.dto.AuthCommonResponse;
import one.dfy.bily.api.auth.dto.SendEmail;
import one.dfy.bily.api.auth.dto.TokenResponse;
import one.dfy.bily.api.auth.mapper.AuthMapper;
import one.dfy.bily.api.auth.model.AuthToken;
import one.dfy.bily.api.auth.model.EmailVerification;
import one.dfy.bily.api.auth.model.repository.AuthTokenRepository;
import one.dfy.bily.api.auth.model.repository.BusinessCardRepository;
import one.dfy.bily.api.auth.model.repository.EmailVerificationRepository;
import one.dfy.bily.api.common.service.EmailService;
import one.dfy.bily.api.common.dto.FileUploadInfo;
import one.dfy.bily.api.user.constant.Role;
import one.dfy.bily.api.user.model.User;
import one.dfy.bily.api.util.JwtProvider;
import one.dfy.bily.api.util.S3Uploader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final S3Uploader s3Uploader;

    private final EmailVerificationRepository emailVerificationRepository;
    private final EmailService emailService;
    private final BusinessCardRepository businessCardRepository;
    private final AuthTokenRepository authTokenRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public void sendEmailVerification(SendEmail request) {
        String verificationCode = generateVerificationCode(6);

        StringBuilder html = new StringBuilder();
        html.append("<html><body>")
                .append("<h1>BILY SEOUL</h1>")
                .append("<p>회원가입을 완료하려면 아래의 인증 코드를 입력하세요:</p>")
                .append("<div style=\"display: flex; justify-content: center; margin-top: 20px;\">");

        for (char ch : verificationCode.toCharArray()) {
            html.append(String.format(
                    "<div style=\"width: 40px; height: 50px; border: 1px solid #ccc; " +
                            "border-radius: 5px; margin: 0 5px; text-align: center; font-size: 20px; " +
                            "line-height: 50px;\">%s</div>", ch));
        }

        html.append("</div></body></html>");

        emailService.sendEmail(request.email(), "BILY SEOUL 이메일 인증", html.toString());

        emailVerificationRepository.save(new EmailVerification(request.email(), verificationCode));
    }

    private String generateVerificationCode(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            sb.append(characters.charAt(randomIndex));
        }

        return sb.toString();
    }


    @Transactional
    public AuthCommonResponse emailVerification(String email, String code) {
        EmailVerification emailVerificationEntity = emailVerificationRepository.findByEmailAndCode(email, code)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 인증 코드입니다."));

        emailVerificationEntity.updateVerified(true);

        return new AuthCommonResponse(true, "이메일 전송이 완료되었습니다.");
    }

    public void createBusinessCard(MultipartFile file, Long userId) {
        FileUploadInfo fileUploadInfo = s3Uploader.businessCardUpload(file);
        businessCardRepository.save(AuthMapper.toBusinessCard(fileUploadInfo, userId));
    }

    @Transactional
    public TokenResponse createRefreshToken(User user) {
        authTokenRepository.findByUserAndUsed(user, true).ifPresent(authToken -> {
            authToken.updateUsed(false);
        });

        String accessToken = jwtProvider.createAccessToken(user.getId(), Role.USER);

        Date date = new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7);
        String refreshToken = jwtProvider.createRefreshToken(user.getId(), date);
        LocalDateTime expiresAt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());

        AuthToken authToken = AuthMapper.toAuthToken(user, refreshToken, expiresAt);

        authTokenRepository.save(authToken);

        return new TokenResponse(user.getName(), accessToken, refreshToken);
    }

    @Transactional
    public void updateEmailUserId(Long userId, String email) {
        EmailVerification emailVerificationEntity = emailVerificationRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 이메일입니다."));
        emailVerificationEntity.updateUserId(userId);
    }

}
