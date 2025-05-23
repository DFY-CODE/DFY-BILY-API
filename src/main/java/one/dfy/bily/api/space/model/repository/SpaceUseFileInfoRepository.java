package one.dfy.bily.api.space.model.repository;

import one.dfy.bily.api.space.model.SpaceUseFileInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpaceUseFileInfoRepository extends JpaRepository<SpaceUseFileInfo, Long> {
    Optional<SpaceUseFileInfo> findBySpaceIdAndUsedOrderByFileOrderAsc(Long spaceId, boolean used);
}
