package one.dfy.bily.api.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.dfy.bily.api.auth.dto.*;
import one.dfy.bily.api.auth.facade.AuthFacade;
import one.dfy.bily.api.auth.service.AuthService;
import one.dfy.bily.api.util.IpUtils;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<AuthCommonResponse> sendEmailVerification(@RequestBody SendEmail request) {
        authService.sendEmailVerification(request);

        return ResponseEntity.ok(new AuthCommonResponse(true, "이메일 전송이 완료되었습니다."));
    }

    @GetMapping(value = "/verification-email")
    @Operation(summary = "이메일 인증 코드 확인", description = "인증 코드를 확인합니다.")
    @ApiResponse(responseCode = "200", description = "이메일 인증 성공")
    public ResponseEntity<AuthCommonResponse> emailVerification(@RequestParam String email, @RequestParam String code) {

        return ResponseEntity.ok(authService.emailVerification(email, code));
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
                    schema = @Schema(implementation = TokenResponse.class),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            externalValue = "/swagger/json/inquiry/findInquiryByInquiryId.json"
                    )
            )
    )
    public ResponseEntity<TokenResponse> signUp(
            @RequestPart("data") SignUpRequest request,
            @RequestPart("businessCard") MultipartFile businessCard,
            HttpServletRequest httpRequest) {
        String clientIp = IpUtils.getClientIp(httpRequest);
        return ResponseEntity.ok(authFacade.signUp(request, businessCard, clientIp));
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
    public ResponseEntity<TokenResponse> signIn(@RequestBody SignInRequest request, HttpServletRequest httpRequest) {
        String clientIp = IpUtils.getClientIp(httpRequest);
        return ResponseEntity.ok(authFacade.signIn(request, clientIp));
    }


}
