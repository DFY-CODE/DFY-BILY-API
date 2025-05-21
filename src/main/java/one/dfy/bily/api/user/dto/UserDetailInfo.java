package one.dfy.bily.api.user.dto;

import java.time.LocalDateTime;

public record UserDetailInfo(
        String email,
        String name,
        String phoneNumber,
        LocalDateTime createAt,
        LocalDateTime recentLoginAt,
        String businessCardUrl
) {
}
