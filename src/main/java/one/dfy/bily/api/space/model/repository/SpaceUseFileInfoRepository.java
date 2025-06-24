package one.dfy.bily.api.space.model.repository;

import jakarta.transaction.Transactional;
import one.dfy.bily.api.space.model.SpaceFileInfo;
import one.dfy.bily.api.space.model.SpaceUseFileInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SpaceUseFileInfoRepository extends JpaRepository<SpaceUseFileInfo, Long> {
    // ✔️ 여러 건 조회
    List<SpaceUseFileInfo> findAllBySpaceIdAndUsedOrderByFileOrderAsc(Long spaceId, boolean used);

    // 추가 – id(Primary Key) 만 조회
    @Query("select s.id from SpaceUseFileInfo s " +
            "where s.spaceId = :spaceId and s.used = :used " +
            "order by s.fileOrder asc")
    List<Long> findIdsBySpaceIdAndUsedOrderByFileOrderAsc(@Param("spaceId") Long spaceId,
                                                          @Param("used") boolean used);


    /* spaceId 로 전체 사용 사례 이미지 조회 */
    List<SpaceUseFileInfo> findBySpaceId(Long spaceId);

    Optional<SpaceUseFileInfo> findBySaveFileName(String saveFileName);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
    update SpaceUseFileInfo f
       set f.fileOrder = :fileOrder
     where f.id      = :id
       and f.spaceId  = :spaceId
""")
    void updateFileOrder(@Param("id")  Long id,
                    @Param("spaceId")  Long spaceId,
                    @Param("fileOrder") int  fileOrder);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
    update SpaceUseFileInfo f
       set f.fileOrder = :fileOrder,
           f.fileTitle = :title
     where f.id      = :id
       and f.spaceId  = :spaceId
""")
    void updateMeta(@Param("id")  Long id,
                    @Param("spaceId")  Long spaceId,
                    @Param("fileOrder") int  fileOrder,
                    @Param("title")    String title);



}
