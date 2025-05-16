package one.dfy.bily.api.auth.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.dfy.bily.api.common.model.BaseEntity;

@Entity
@Table(name = "TBL_SIGN_UP_EMAIL_VERIFICATION")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignUpEmailVerification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CODE")
    private String code;

    @Column(name = "IS_VERIFIED")
    private Boolean verified;

    @Column(name = "EMAIL", length = 100, nullable = false)
    private String email;

    public SignUpEmailVerification(String code, String email) {
        this.code = code;
        this.verified = false;
        this.email = email;
    }

    public void updateVerified(boolean isVerified){
        this.verified = isVerified;
    }
}
