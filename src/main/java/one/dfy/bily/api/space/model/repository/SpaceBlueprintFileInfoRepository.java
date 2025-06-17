package one.dfy.bily.api.space.model.repository;

import one.dfy.bily.api.space.dto.SpaceBlueprintFileInfo;
import one.dfy.bily.api.space.model.SpaceBlueprintFile;
import one.dfy.bily.api.space.model.SpaceUseFileInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpaceBlueprintFileInfoRepository extends JpaRepository<SpaceBlueprintFile, Long> {
    Optional<SpaceBlueprintFile> findBySpaceIdAndUsed(Long spaceId, Boolean used);

    Optional<SpaceBlueprintFile> findBySpaceId(Long spaceId);

    Optional<SpaceBlueprintFile> findBySaveFileName(String saveFileName);

    Optional<SpaceBlueprintFile> findFirstBySpaceId(Long spaceId);


}
