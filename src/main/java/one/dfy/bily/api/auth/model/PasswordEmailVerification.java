package one.dfy.bily.api.auth.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.dfy.bily.api.common.model.BaseEntity;

@Entity
@Table(name = "TBL_PASSWORD_RESET_EMAIL_VERIFICATION")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PasswordEmailVerification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CODE")
    private String code;

    @Column(name = "IS_USED")
    private Boolean used;

    @Column(name = "EMAIL", length = 100, nullable = false)
    private String email;

    public PasswordEmailVerification(String code, String email) {
        this.code = code;
        this.used = true;
        this.email = email;
    }

    public void updateUsed(boolean used){
        this.used = used;
    }

}
