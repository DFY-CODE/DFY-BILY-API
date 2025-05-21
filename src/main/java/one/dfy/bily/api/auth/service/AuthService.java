package one.dfy.bily.api.auth.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.auth.dto.*;
import one.dfy.bily.api.auth.mapper.AuthMapper;
import one.dfy.bily.api.auth.model.AuthToken;
import one.dfy.bily.api.auth.model.PasswordEmailVerification;
import one.dfy.bily.api.auth.model.PasswordResetToken;
import one.dfy.bily.api.auth.model.SignUpEmailVerification;
import one.dfy.bily.api.auth.model.repository.*;
import one.dfy.bily.api.common.service.EmailService;
import one.dfy.bily.api.common.dto.FileUploadInfo;
import one.dfy.bily.api.user.constant.Role;
import one.dfy.bily.api.user.model.User;
import one.dfy.bily.api.user.model.repository.BusinessCardRepository;
import one.dfy.bily.api.util.JwtCookieUtil;
import one.dfy.bily.api.util.JwtProvider;
import one.dfy.bily.api.util.S3Uploader;
import one.dfy.bily.api.util.TokenGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final S3Uploader s3Uploader;

    private final SignUpEmailVerificationRepository signUpEmailVerificationRepository;
    private final EmailService emailService;
    private final BusinessCardRepository businessCardRepository;
    private final AuthTokenRepository authTokenRepository;
    private final PasswordEmailVerificationRepository passwordEmailVerificationRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final JwtProvider jwtProvider;
    private final TokenGenerator tokenGenerator;
    private final JwtCookieUtil jwtCookieUtil;

    @Transactional
    public AuthCommonResponse sendSignUpEmailVerification(SendEmail request) {
        String verificationCode = sendEmailVerification(request.email());

        signUpEmailVerificationRepository.save(new SignUpEmailVerification(verificationCode, request.email()));

        return new AuthCommonResponse(true, "이메일 전송이 완료되었습니다.");
    }

    @Transactional
    public AuthCommonResponse sendPasswordResetVerification(SendEmail request) {
        String verificationCode = sendEmailVerification(request.email());

        passwordEmailVerificationRepository.save(new PasswordEmailVerification(verificationCode, request.email()));

        return new AuthCommonResponse(true, "이메일 전송이 완료되었습니다.");
    }

    private String sendEmailVerification(String email) {
        // 인증 코드 생성
        String verificationCode = generateVerificationCode(6);

        // HTML 본문 생성
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

        // 이메일 제목에 인증 코드 추가
        String emailSubject = String.format("BILY SEOUL 이메일 인증 [%s]", verificationCode);

        // 이메일 전송
        emailService.sendEmail(email, emailSubject, html.toString());

        return verificationCode;
    }


    private String generateVerificationCode(int length) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int digit = (int) (Math.random() * 10); // 0에서 9까지의 랜덤 숫자 생성
            code.append(digit);
        }
        return code.toString();
    }

    @Transactional
    public AuthCommonResponse signUpEmailVerification(String email, String code) {
        SignUpEmailVerification signUpEmailVerificationEntity = signUpEmailVerificationRepository.findByEmailAndCode(email, code)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 인증 코드입니다."));

        signUpEmailVerificationEntity.updateVerified(true);

        return new AuthCommonResponse(true, "이메일 안중이 완료되었습니다.");
    }

    @Transactional
    public PasswordResetTokenResponse passwordResetEmailVerification(EmailVerificationInfo request) {
        PasswordEmailVerification passwordEmailVerification = passwordEmailVerificationRepository.findByEmailAndCodeAndUsed(request.email(), request.code(), true)
                .orElseThrow(() -> new IllegalArgumentException("인증이 만료되었습니다."));

        passwordEmailVerification.updateUsed(false);

        String token = tokenGenerator.generate();

        passwordResetTokenRepository.save(new PasswordResetToken(token, request.email()));

        return new PasswordResetTokenResponse(token);
    }

    @Transactional(readOnly = true)
    public PasswordResetToken checkPasswordResetToken(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByTokenAndUsed(token, true)
                .orElseThrow(() -> new IllegalArgumentException("인증정보가 올바르지 않습니다"));
        passwordResetToken.updateUsed(false);

        return passwordResetToken;
    }

    public void createBusinessCard(MultipartFile file, Long userId) {
        if(file ==  null || file.isEmpty()) {
            return;
        }
        FileUploadInfo fileUploadInfo = s3Uploader.businessCardUpload(file);
        businessCardRepository.save(AuthMapper.toBusinessCard(fileUploadInfo, userId));
    }

    @Transactional
    public JwtResponse createRefreshToken(User user, HttpServletResponse response) {
        authTokenRepository.findByUserAndUsed(user, true).ifPresent(authToken -> {
            authToken.updateUsed(false);
        });

        String accessToken = jwtProvider.createAccessToken(user.getId(), Role.USER);
        jwtCookieUtil.setAccessTokenCookie(response, accessToken);

        Date date = new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7);
        String refreshToken = jwtProvider.createRefreshToken(user.getId(), date);
        LocalDateTime expiresAt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());

        AuthToken authToken = AuthMapper.toAuthToken(user, refreshToken, expiresAt);

        authTokenRepository.save(authToken);

        return new JwtResponse(user.getName(), accessToken, refreshToken);
    }

    @Transactional
    public void updateEmailUserId(String email, String code) {
        if(!signUpEmailVerificationRepository.existsByEmailAndCodeAndVerified(email, code, true)){
            throw new IllegalArgumentException("인증되지 않은 이메일 입니다.");
        }
    }

}
