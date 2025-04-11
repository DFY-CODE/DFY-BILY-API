package one.dfy.bily.api.common.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminUser {
    private Long userId;
    private String userName;
    private String email;
    private String password;
    private String accessToken;
    private String refreshToken;
    private LocalDateTime expirationAt;
}

