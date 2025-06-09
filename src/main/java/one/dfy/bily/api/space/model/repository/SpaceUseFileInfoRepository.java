package one.dfy.bily.api.space.model.repository;

import one.dfy.bily.api.space.model.SpaceUseFileInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpaceUseFileInfoRepository extends JpaRepository<SpaceUseFileInfo, Long> {
    // ✔️ 여러 건 조회
    List<SpaceUseFileInfo> findAllBySpaceIdAndUsedOrderByFileOrderAsc(Long spaceId, boolean used);

}
