package one.dfy.bily.api.user.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.dfy.bily.api.common.model.BaseEntity;
import one.dfy.bily.api.user.constant.LoginStatus;

@Entity
@Table(name = "TBL_LOGIN_HISTORY",
        indexes = @Index(name = "idx_user_id", columnList = "user_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USER_ID")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_status", nullable = false, length = 20)
    private LoginStatus loginStatus;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    public LoginHistory(Long userId, LoginStatus loginStatus, String ipAddress) {
        this.userId = userId;
        this.loginStatus = loginStatus;
        this.ipAddress = ipAddress;
    }

    public void changeLoginStatus(LoginStatus loginStatus) {
        this.loginStatus = loginStatus;
    }
}