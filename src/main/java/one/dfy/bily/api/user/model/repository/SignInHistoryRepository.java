package one.dfy.bily.api.user.model.repository;

import one.dfy.bily.api.user.model.SignInHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SignInHistoryRepository extends JpaRepository<SignInHistory, Long> {
    Optional<SignInHistory> findTopByUserIdOrderByIdDesc(Long userId);
}
