package one.dfy.bily.api.space.model.repository;

import one.dfy.bily.api.space.model.Space;
import one.dfy.bily.api.space.model.SavedSpace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SavedSpaceRepository extends JpaRepository<SavedSpace, Long> {
    Optional<SavedSpace> findBySpaceAndUserId(Space space, Long userId);
    Page<SavedSpace> findByUserId(Long userId, Pageable pageable);
}
