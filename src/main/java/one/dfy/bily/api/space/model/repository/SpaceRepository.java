package one.dfy.bily.api.space.model.repository;

import one.dfy.bily.api.space.model.Space;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpaceRepository extends JpaRepository<Space,Long> {
    Page<Space> findByTitleContainingAndDisplayStatus(String alias, Boolean displayStatus, Pageable pageable);
    Page<Space> findByDisplayStatus(Boolean displayStatus, Pageable pageable);
    Page<Space> findByTitleContaining(String alias, Pageable pageable);

}
