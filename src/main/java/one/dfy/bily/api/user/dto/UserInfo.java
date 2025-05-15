package one.dfy.bily.api.user.dto;

import java.time.LocalDateTime;

public record UserInfo(
        Long userId,
        LocalDateTime signupAt,
        LocalDateTime recentLoginAt,
        String email,
        String name,
        String phoneNumber
) {
}
