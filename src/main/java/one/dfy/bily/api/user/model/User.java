package one.dfy.bily.api.user.model;

import jakarta.persistence.*;
import lombok.*;
import one.dfy.bily.api.common.model.BaseEntity;
import one.dfy.bily.api.user.constant.Role;
import one.dfy.bily.api.user.constant.UserStatus;

import java.time.LocalDate;

@Entity
@Table(name = "TBL_USER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", length = 50)
    private String name;

    @Column(name = "PASSWORD", length = 255)
    private String password;

    @Column(name = "EMAIL", length = 100, nullable = false)
    private String email;

    @Column(name = "PHONE_NUMBER", length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "IS_USED")
    private UserStatus status = UserStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE")
    private Role role;

    public User(String name, String password, String email, String phoneNumber, Role role) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public void updateStatus(UserStatus status) {
        this.status = status;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
