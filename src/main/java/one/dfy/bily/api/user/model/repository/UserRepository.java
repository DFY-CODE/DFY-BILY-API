package one.dfy.bily.api.user.model.repository;

import one.dfy.bily.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
