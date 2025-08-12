package one.dfy.bily.api.auth.dto;

public record JwtResponse(
        Long userId,
        String userName,
        String accessToken,
        String refreshToken,
        one.dfy.bily.api.user.constant.Role role
) {
}
