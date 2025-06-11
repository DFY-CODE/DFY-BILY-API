package one.dfy.bily.api.space.model.repository;

import one.dfy.bily.api.space.model.SpaceBlueprintFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpaceBlueprintFileInfoRepository extends JpaRepository<SpaceBlueprintFile, Long> {
    Optional<SpaceBlueprintFile> findBySpaceIdAndUsed(Long spaceId, Boolean used);
}
