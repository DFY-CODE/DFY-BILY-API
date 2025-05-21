package one.dfy.bily.api.space.model.repository;

import one.dfy.bily.api.space.model.SpaceFileInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SpaceFileInfoRepository extends JpaRepository<SpaceFileInfo, Integer> {
    List<SpaceFileInfo> findBySpaceIdInAndUsedAndThumbnail(List<Long> spaceIds, boolean used, boolean thumbnail);
    List<SpaceFileInfo> findBySpaceIdAndUsed(Long spaceId, boolean used);
}
