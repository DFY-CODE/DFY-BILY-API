package one.dfy.bily.api.auth.dto;

public record EmailVerificationInfo(
        String email,
        String code
) {
}
