package one.dfy.bily.api.auth.dto;

public record TokenResponse(
        String userName,
        String accessToken,
        String refreshToken
) {
}
