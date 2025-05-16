package one.dfy.bily.api.auth.dto;

public record JWTResponse(
        String userName,
        String accessToken,
        String refreshToken
) {
}
