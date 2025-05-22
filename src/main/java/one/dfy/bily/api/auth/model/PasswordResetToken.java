package one.dfy.bily.api.auth.model;

import jakarta.persistence.*;
import lombok.*;
import one.dfy.bily.api.common.model.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TBL_PASSWORD_RESET_TOKEN")
public class PasswordResetToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String token;

    @Column(nullable = false)
    private String email;

    @Column(name = "EXPIRES_AT", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "IS_USED", nullable = false)
    private boolean used = true;

    public PasswordResetToken(String token, String email) {
        this.token = token;
        this.email = email;
        this.expiresAt = LocalDateTime.now().plusMinutes(5);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    public void updateUsed(boolean used) {
        this.used = used;
    }
}
