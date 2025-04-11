package one.dfy.bily.api.common.dto;

import lombok.Data;
import java.util.Date;

@Data
public class User {
    private Long userId;
    private String userName;
    private String password;
    private String accessToken;
    private String refreshToken;
    private Date expirationAt;
    private Date createdAt;
    private Date updatedAt;
    private String email;
    private String verificationCode;
    private String phoneNumber;
    private String gender;
    private Date birthDate;
    // ... 추가 필드
    private String saveLocation; // 추가: 명함 사진 저장 위치
}
