package one.dfy.bily.api.auth.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.dfy.bily.api.common.model.BaseEntity;

@Entity
@Table(name = "TBL_EMAIL_VERIFICATION")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVerification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "CODE")
    private String code;

    @Column(name = "IS_VERIFIED")
    private Boolean verified;

    @Column(name = "EMAIL", length = 100, nullable = false)
    private String email;

    public EmailVerification(String code, String email) {
        this.code = code;
        this.verified = false;
        this.email = email;
    }

    public void updateVerified(boolean isVerified){
        this.verified = isVerified;
    }
}
