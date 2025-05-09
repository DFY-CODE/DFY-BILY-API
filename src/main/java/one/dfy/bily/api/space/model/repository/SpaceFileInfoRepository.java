package one.dfy.bily.api.space.model.repository;

import one.dfy.bily.api.space.model.SpaceFileInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SpaceFileInfoRepository extends JpaRepository<SpaceFileInfo, Integer> {
    @Query("""
    SELECT s FROM SpaceFileInfo s
    WHERE s.id IN (
        SELECT MIN(s2.id)
        FROM SpaceFileInfo s2
        WHERE s2.contentId IN :contentIds
        GROUP BY s2.contentId
    )
""")
    List<SpaceFileInfo> findFirstByContentIdGroup(@Param("contentIds") List<Integer> contentIds);
}
