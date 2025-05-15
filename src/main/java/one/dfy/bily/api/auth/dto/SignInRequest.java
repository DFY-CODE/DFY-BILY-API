package one.dfy.bily.api.auth.dto;

public record SignInRequest(
        String email,
        String password
) {
}
