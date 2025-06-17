package one.dfy.bily.api.space.model.repository;

import one.dfy.bily.api.space.model.SpaceFileInfo;
import one.dfy.bily.api.space.model.SpaceUseFileInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpaceFileInfoRepository extends JpaRepository<SpaceFileInfo, Long> {
    List<SpaceFileInfo> findBySpaceIdInAndUsedAndThumbnail(List<Long> spaceIds, boolean used, boolean thumbnail);


    List<SpaceFileInfo> findBySpaceIdInAndThumbnail(List<Long> spaceIds,
                                                    boolean thumbnail);


    List<SpaceFileInfo> findBySpaceIdAndUsedOrderByFileOrderAsc(Long spaceId, boolean used);

    /* spaceId 로 전체 사용 사례 이미지 조회 */
    Optional<SpaceFileInfo> findBySaveFileName(String saveFileName);

    /** 특정 공간의 모든 이미지 조회 */
    List<SpaceFileInfo> findBySpaceId(Long spaceId);


}
