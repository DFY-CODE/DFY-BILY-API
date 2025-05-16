package one.dfy.bily.api.auth.model.repository;

import one.dfy.bily.api.auth.model.PasswordEmailVerification;
import one.dfy.bily.api.auth.model.SignUpEmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordEmailVerificationRepository extends JpaRepository<PasswordEmailVerification, Long> {
    Optional<PasswordEmailVerification> findByEmailAndCodeAndUsed(String email, String code, boolean used);
}
