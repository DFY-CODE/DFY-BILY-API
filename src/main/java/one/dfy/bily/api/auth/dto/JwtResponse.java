package one.dfy.bily.api.auth.dto;

public record JwtResponse(
        String userName,
        String accessToken,
        String refreshToken
) {
}
