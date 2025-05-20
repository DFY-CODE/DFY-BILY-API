package one.dfy.bily.api.user.model.repository;

import one.dfy.bily.api.user.constant.UserStatus;
import one.dfy.bily.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserCustomRepository {
    boolean existsByPhoneNumberAndStatus(String phoneNumber,UserStatus status);
    Optional<User> findByEmailAndStatus(String email, UserStatus status);
    boolean existsByEmailAndStatus(String email, UserStatus status);
    Optional<User> findByNameAndPhoneNumber(String name, String phoneNumber);
}
