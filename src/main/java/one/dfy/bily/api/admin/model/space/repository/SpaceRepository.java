package one.dfy.bily.api.admin.model.space.repository;

import one.dfy.bily.api.admin.model.space.Space;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpaceRepository extends JpaRepository<Space,Long> {
    Optional<Space> findByContentId(long contentId);
}
