package one.dfy.bily.api.auth.dto;

public record JwtResponse(
        Long userId,
        String userName,
        String accessToken,
        String refreshToken
) {
}
