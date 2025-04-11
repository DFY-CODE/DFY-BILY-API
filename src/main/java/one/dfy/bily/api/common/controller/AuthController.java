package one.dfy.bily.api.common.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import one.dfy.bily.api.common.dto.AdminUser;
import one.dfy.bily.api.common.dto.AuthRequest;
import one.dfy.bily.api.common.request.EmailVerificationRequest;
import one.dfy.bily.api.common.mapper.EmailVerificationMapper;
import one.dfy.bily.api.common.service.UserService;
import one.dfy.bily.api.common.service.EmailService;
import one.dfy.bily.api.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("/api/auth")
@Tag(name = "인증 API", description = "사용자 인증 관련 API")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailVerificationMapper emailVerificationMapper;

    @Autowired
    private EmailService emailService;

    @Operation(summary = "로그인", description = "사용자 로그인 및 JWT 토큰 발급")
    @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    @ApiResponse(responseCode = "401", description = "잘못된 이메일 또는 비밀번호", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        log.info("🚀 로그인 요청: username = {}, password = {}, email = {}", authRequest.getUsername(), authRequest.getPassword(), authRequest.getEmail());

        AdminUser user = userService.findByEmail(authRequest.getEmail());

        if (user == null || !passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            log.warn("로그인 실패: 잘못된 정보");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid email or password"));
        }

        // JWT 생성
        String accessToken = jwtUtil.createAccessToken(user.getUserName());
        String refreshToken = jwtUtil.createRefreshToken(user.getUserName());

        if (accessToken == null || refreshToken == null) {
            log.error("JWT 생성 실패: Access Token 또는 Refresh Token이 null입니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Token generation failed."));
        }

        // 로그를 통해 문제 값 확인
        log.info("✅ 로그인 성공, Access Token: {}", accessToken);
        log.info("🔄 Refresh Token 발급: {}", refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Refresh-Token", refreshToken);

        // Null-safe Map 생성
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("userId", user.getUserId() != null ? user.getUserId() : -1); // 기본값 -1
        responseBody.put("userName", user.getUserName() != null ? user.getUserName() : "Unknown");
        responseBody.put("email", user.getEmail() != null ? user.getEmail() : "Unknown");
        responseBody.put("accessToken", accessToken);
        responseBody.put("refreshToken", refreshToken);

        return ResponseEntity.ok()
                .headers(headers)
                .body(responseBody);
    }



    @Operation(summary = "이메일 인증 코드 전송", description = "사용자 이메일로 인증 코드를 전송합니다.")
    @ApiResponse(responseCode = "200", description = "이메일 전송 성공")
    @ApiResponse(responseCode = "500", description = "이메일 전송 실패")
    @PostMapping(value = "/send-verification-email", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendVerificationEmail(@RequestBody String email) {
        String token = UUID.randomUUID().toString();

        // 6자리 인증 코드 생성
        String verificationCode = generateVerificationCode(6);

        // 인증코드 유효시간 10분
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(10);
        // LocalDateTime expirationTime = LocalDateTime.now().plusHours(24);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(email);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("잘못된 요청 형식입니다.");
        }

        String extractedEmail = jsonNode.get("email").asText();

        // 이메일 전송 시도 : 인증 토큰방식
         /*String verificationLink = "http://localhost:8081/api/auth/verify-email?token=" + token;
            boolean emailSent = emailService.sendEmail(
                extractedEmail,
                "BILY SEOUL 이메일 인증",
                "회원가입을 완료하려면 아래 링크를 클릭하세요:\n" + verificationLink
        );*/

        String htmlContent = "<html><body>"
                + "<h1>BILY SEOUL</h1>"
                + "<p>회원가입을 완료하려면 아래의 인증 코드를 입력하세요:</p>"
                + "<div style=\"display: flex; justify-content: center; margin-top: 20px;\">";

        for (int i = 0; i < verificationCode.length(); i++) {
            htmlContent += "<div style=\"width: 40px; height: 50px; border: 1px solid #ccc; border-radius: 5px; margin: 0 5px; text-align: center; font-size: 20px; line-height: 50px;\">"
                    + verificationCode.charAt(i)
                    + "</div>";
        }

        htmlContent += "</div></body></html>";

// 이메일 전송 시도 : 6자리 인증코드 입력방식
        boolean emailSent = emailService.sendEmail(
                extractedEmail,
                "BILY SEOUL 이메일 인증",
                htmlContent
        );

        // 이메일 전송 성공 시 DB에 저장
        if (emailSent) {
            emailVerificationMapper.insertVerificationToken(extractedEmail, token, verificationCode, expirationTime);
            return ResponseEntity.ok("이메일이 전송되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이메일 전송에 실패했습니다.");
        }
    }

    // 인증 코드 생성 함수
    public static String generateVerificationCode(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            sb.append(characters.charAt(randomIndex));
        }

        return sb.toString();
    }

    @Operation(
            summary = "이메일 인증 코드 검증",
            description = "사용자가 입력한 인증 코드를 검증합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "이메일 인증 요청 데이터",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EmailVerificationRequest.class),
                            examples = @ExampleObject(value = "{ \"email\": \"user@example.com\", \"verificationCode\": \"123456\" }")
                    )
            )
    )
    @ApiResponse(responseCode = "200", description = "이메일 인증 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 인증 코드")
    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@Valid @RequestBody EmailVerificationRequest request) {
        return emailVerificationMapper.verifyCode(request.getEmail(), request.getVerificationCode())
                ? ResponseEntity.ok("이메일 인증이 완료되었습니다.")
                : ResponseEntity.badRequest().body("잘못된 인증 코드입니다.");
    }

    @Operation(summary = "회원가입", description = "사용자 회원가입")
    @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    @ApiResponse(responseCode = "400", description = "회원가입 실패")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        try {
           userService.updatePassword(
                    request.getEmail(),
                    request.getPassword(),
                    request.getUsername(),
                    request.getGender(),
                    request.getBirthDate()
            );

            AdminUser user = userService.findByEmail( request.getEmail());

            // 회원가입 성공 시 userId를 JSON 형태로 반환
            Map<String, Long> response = new HashMap<>();
            response.put("userId", user.getUserId());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // 회원가입 실패 시 에러 메시지 반환 (필요에 따라 더 상세한 에러 처리 가능)
            return ResponseEntity.badRequest().body("회원가입 실패!");
        }
    }



}

