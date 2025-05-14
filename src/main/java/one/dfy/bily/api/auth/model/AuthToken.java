package one.dfy.bily.api.auth.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.dfy.bily.api.common.model.BaseEntity;
import one.dfy.bily.api.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "TBL_AUTH_TOKEN")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID", nullable = false)
    private User user;

    @Column(name = "REFRESH_TOKEN")
    private String refreshToken;

    @Column(name = "EXPIRES_AT")
    private LocalDateTime expiresAt;

    @Column(name = "IS_USED")
    private Boolean used = true;

    public AuthToken(User user, String refreshToken, LocalDateTime expiresAt) {
        this.user = user;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
    }

    public void updateUsed(boolean used) {
        this.used = used;
    }
}
