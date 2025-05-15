package one.dfy.bily.api.user.model.repository;

import one.dfy.bily.api.user.model.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
    Optional<LoginHistory> findTopByUserIdOrderByIdDesc(Long userId);
}
