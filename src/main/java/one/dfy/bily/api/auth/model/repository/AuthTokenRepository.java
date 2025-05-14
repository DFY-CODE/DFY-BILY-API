package one.dfy.bily.api.auth.model.repository;

import one.dfy.bily.api.auth.model.AuthToken;
import one.dfy.bily.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {
    Optional<AuthToken> findByUserAndUsed(User user, boolean used);
}
