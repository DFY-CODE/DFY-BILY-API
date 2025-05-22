package one.dfy.bily.api.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.dfy.bily.api.auth.dto.*;
import one.dfy.bily.api.auth.facade.AuthFacade;
import one.dfy.bily.api.auth.service.AuthService;
import one.dfy.bily.api.security.CustomUserDetails;
import one.dfy.bily.api.user.dto.UserCommonResponse;
import one.dfy.bily.api.util.IpUtils;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@Tag(name = "인증 API", description = "사용자 인증 관련 API")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthFacade authFacade;

    @PostMapping(value = "/send-verification-email")
    @Operation(summary = "이메일 인증 코드 전송", description = "사용자 이메일로 인증 코드를 전송합니다.")
    @ApiResponse(responseCode = "200", description = "이메일 전송 성공")
    @ApiResponse(responseCode = "500", description = "이메일 전송 실패")
    public ResponseEntity<AuthCommonResponse> sendSignUpEmailVerification(@RequestBody SendEmail request) {
        return ResponseEntity.ok(authFacade.sendSignUpEmailVerification(request));
    }

    @GetMapping(value = "/verification-email")
    @Operation(summary = "이메일 인증 코드 확인", description = "인증 코드를 확인합니다.")
    @ApiResponse(responseCode = "200", description = "이메일 인증 성공")
    public ResponseEntity<AuthCommonResponse> emailVerification(@RequestParam String email, @RequestParam String code) {

        return ResponseEntity.ok(authService.signUpEmailVerification(email, code));
    }

    @GetMapping(value = "/phone-number")
    @Operation(summary = "휴대폰 번호 중복 확인", description = "휴대폰 번호 중복 여부를 확인합니다.")
    @ApiResponse(responseCode = "200", description = "이메일 인증 성공")
    public ResponseEntity<AuthCommonResponse> checkPhoneNumber(@RequestParam("phoneNumber") String phoneNumber) {
        return ResponseEntity.ok(authFacade.checkPhoneNumber(phoneNumber));
    }

    @PostMapping(value = "/sign-up", consumes = "multipart/form-data")
    @Operation(summary = "회원 가입", description = "회원 가입 후 토큰을 반환합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = JwtResponse.class),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            externalValue = "/swagger/json/inquiry/findInquiryByInquiryId.json"
                    )
            )
    )
    public ResponseEntity<JwtResponse> signUp(
            @RequestPart("data") SignUpRequest request,
            @RequestPart(name = "businessCard", required = false) MultipartFile businessCard,
            HttpServletRequest httpRequest,
            HttpServletResponse response
    ) {
        String clientIp = IpUtils.getClientIp(httpRequest);
        return ResponseEntity.ok(authFacade.signUp(request, businessCard, clientIp, response));
    }

    @PostMapping(value = "/sign-in")
    @Operation(summary = "로그인", description = "로그인 후 토큰을 반환합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SignUpRequest.class),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            externalValue = "/swagger/json/inquiry/findInquiryByInquiryId.json"
                    )
            )
    )
    public ResponseEntity<JwtResponse> signIn(@RequestBody SignInRequest request,
                                              HttpServletRequest httpRequest,
                                              HttpServletResponse response
    ) {
        String clientIp = IpUtils.getClientIp(httpRequest);
        return ResponseEntity.ok(authFacade.signIn(request, clientIp, response));
    }


    @PostMapping(value = "/password/send-verification-email")
    @Operation(summary = "비밀번호 찾기 이메일 인증 코드 전송", description = "회원 이메일로 인증 코드를 전송합니다.")
    @ApiResponse(responseCode = "200", description = "이메일 전송 성공")
    @ApiResponse(responseCode = "500", description = "이메일 전송 실패")
    public ResponseEntity<AuthCommonResponse> sendPasswordResetEmailVerification(@RequestBody SendEmail request) {

        return ResponseEntity.ok(authFacade.sendPasswordResetVerification(request));
    }


    @PostMapping(value = "/password/verification-email")
    @Operation(summary = "비밀번호 찾기 이메일 인증 확인", description = "회원 이메일로 인증 코드를 확인합니다.")
    @ApiResponse(responseCode = "200", description = "이메일 인증 확인 성공")
    @ApiResponse(responseCode = "500", description = "이메일 인증 확인 실패")
    public ResponseEntity<PasswordResetTokenResponse> passwordResetEmailVerification(@RequestBody EmailVerificationInfo request) {

        return ResponseEntity.ok(authService.passwordResetEmailVerification(request));
    }

    @PostMapping(value = "/password")
    @Operation(summary = "비밀번호 초기화", description = "회원 비밀번호를 초기화합니다.")
    @ApiResponse(responseCode = "200", description = "비밀번호 초기화 성공")
    @ApiResponse(responseCode = "500", description = "비밀번호 초기화 실패")
    public ResponseEntity<UserCommonResponse> resetPassword(@RequestBody PasswordResetRequest request) {

        return ResponseEntity.ok(authFacade.resetPassword(request));
    }

    @GetMapping(value = "/check")
    @Operation(summary = "로그인 여부 확인", description = "로그인 여부를 확인합니다.")
    @ApiResponse(responseCode = "200", description = "비밀번호 초기화 성공")
    @ApiResponse(responseCode = "500", description = "비밀번호 초기화 실패")
    public ResponseEntity<CheckSignIn> resetPassword(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        return ResponseEntity.ok(authFacade.checkSignIn(userId));
    }

    @PostMapping(value = "/sign-out")
    @Operation(summary = "로그아웃", description = "로그아웃 합니다.")
    public ResponseEntity<AuthCommonResponse> signOut(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("ACCESS_TOKEN", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(new AuthCommonResponse(true, "로그아웃이 완료되었습니다."));
    }

    @GetMapping(value = "/admin")
    @Operation(summary = "운영자 여부 확인", description = "운영자 여부를 확인합니다.")
    @ApiResponse(responseCode = "200", description = "비밀번호 초기화 성공")
    @ApiResponse(responseCode = "500", description = "비밀번호 초기화 실패")
    public ResponseEntity<AuthCommonResponse> checkAdmin(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return ResponseEntity.ok(new AuthCommonResponse(true, "운영자"));
        }
        return ResponseEntity.ok(new AuthCommonResponse(false, "사용자"));
    }
}
