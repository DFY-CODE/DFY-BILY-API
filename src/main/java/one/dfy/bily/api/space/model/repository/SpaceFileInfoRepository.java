package one.dfy.bily.api.space.model.repository;

import jakarta.transaction.Transactional;
import one.dfy.bily.api.space.model.SpaceFileInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

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

    /* ────────────────────────────────────────────────
   파일 메타데이터만 갱신 (파일 업로드 없음)
   ──────────────────────────────────────────────── */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
        update SpaceFileInfo f
           set f.fileOrder = :fileOrder,
               f.thumbnail = :thumbnail
         where f.id = :id
           and f.spaceId  = :spaceId
    """)
    void updateMeta(@Param("id") Long id,
                    @Param("spaceId") Long spaceId,
                    @Param("fileOrder") int fileOrder,
                    @Param("thumbnail") boolean thumbnail);
    /* ---------- 추가: PK(id) 목록만 조회 ---------- */
    @Query("""
        select f.id
          from SpaceFileInfo f
         where f.spaceId = :spaceId
           and f.used    = :used
      order by f.fileOrder asc
    """)
    List<Long> findIdsBySpaceIdAndUsedOrderByFileOrderAsc(@Param("spaceId") Long spaceId,
                                                          @Param("used") boolean used);




}
