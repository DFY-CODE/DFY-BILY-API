package one.dfy.bily.api.space.model.repository;

import one.dfy.bily.api.space.model.Space;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpaceRepository extends JpaRepository<Space,Long> {
    Page<Space> findByAliasContainingAndDisplayStatus(String alias, Boolean displayStatus, Pageable pageable);
    Page<Space> findByDisplayStatus(Boolean displayStatus, Pageable pageable);
    Page<Space> findByAliasContaining(String alias, Pageable pageable);

    /* ------------------------------------------------------------------
      공개(사용중) 공간만 조회
      -> TBL_SPACE.IS_USED = 1
    ------------------------------------------------------------------ */
    List<Space> findAllByDisplayStatusTrue();




}
