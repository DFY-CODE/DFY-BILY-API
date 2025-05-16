package one.dfy.bily.api.auth.model.repository;

import one.dfy.bily.api.auth.model.SignUpEmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SignUpEmailVerificationRepository extends JpaRepository<SignUpEmailVerification, Long> {
    Optional<SignUpEmailVerification> findByEmailAndCode(String email, String code);
    boolean findByEmailAndCodeAndVerified(String email, String code, boolean verified);
}
