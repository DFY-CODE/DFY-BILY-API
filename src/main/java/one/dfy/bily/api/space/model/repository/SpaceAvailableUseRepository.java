package one.dfy.bily.api.space.model.repository;

import one.dfy.bily.api.space.model.SpaceAvailableUse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpaceAvailableUseRepository extends JpaRepository<SpaceAvailableUse, Long> {
    List<SpaceAvailableUse> findBySpaceId(Long spaceId);
}
