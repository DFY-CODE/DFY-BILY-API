package one.dfy.bily.api.space.model.repository;

import one.dfy.bily.api.space.model.Amenity;
import one.dfy.bily.api.space.model.SpaceAmenity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpaceAmenityRepository extends JpaRepository<SpaceAmenity, Long> {
    List<SpaceAmenity> findBySpaceId(Long spaceId);
    void deleteBySpaceId(Long spaceId);
}
