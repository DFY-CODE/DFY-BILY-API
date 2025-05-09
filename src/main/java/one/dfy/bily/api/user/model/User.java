package one.dfy.bily.api.user.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "TBL_USER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @Column(name = "USER_NAME", length = 50)
    private String userName;

    @Column(name = "PASSWORD", length = 255)
    private String password;

    @Column(name = "ACCESS_TOKEN", columnDefinition = "TEXT")
    private String accessToken;

    @Column(name = "REFRESH_TOKEN", columnDefinition = "TEXT")
    private String refreshToken;

    @Column(name = "EXPIRATION_AT")
    private LocalDateTime expirationAt;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "EMAIL", length = 100, nullable = false)
    private String email;

    @Column(name = "VERIFICATION_CODE", length = 6)
    private String verificationCode;

    @Column(name = "PHONE_NUMBER", length = 20)
    private String phoneNumber;

    @Column(name = "GENDER", length = 10)
    private String gender;

    @Column(name = "BIRTH_DATE")
    private LocalDate birthDate;

    @PrePersist
    protected void onCreate() {
        this.createdAt = this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
