package one.dfy.bily.api.common.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationRequest {

    @Schema(
            description = "사용자의 이메일 주소",
            example = "user@example.com"
    )
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @Schema(
            description = "이메일로 전송된 인증 코드 (6자리 숫자)",
            example = "TEST1234"
    )
    @NotBlank(message = "인증 코드는 필수 입력 항목입니다.")
    private String verificationCode;
}


