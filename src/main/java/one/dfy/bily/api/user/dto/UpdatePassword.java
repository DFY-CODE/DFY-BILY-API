package one.dfy.bily.api.user.dto;

public record UpdatePassword(
        String originalPassword,
        String newPassword
) {
}
