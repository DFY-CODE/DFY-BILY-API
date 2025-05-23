package one.dfy.bily.api.space.model.repository;

import one.dfy.bily.api.space.model.SpaceFileInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpaceFileInfoRepository extends JpaRepository<SpaceFileInfo, Long> {
    List<SpaceFileInfo> findBySpaceIdInAndUsedAndThumbnail(List<Long> spaceIds, boolean used, boolean thumbnail);
    List<SpaceFileInfo> findBySpaceIdAndUsedOrderByFileOrderAsc(Long spaceId, boolean used);
}
