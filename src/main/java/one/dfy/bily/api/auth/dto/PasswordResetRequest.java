package one.dfy.bily.api.auth.dto;

public record PasswordResetRequest(
        String token,
        String password
) {
}
