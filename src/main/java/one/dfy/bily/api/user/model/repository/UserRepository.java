package one.dfy.bily.api.user.model.repository;

import one.dfy.bily.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserCustomRepository {
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<User> findByEmail(String email);
}
