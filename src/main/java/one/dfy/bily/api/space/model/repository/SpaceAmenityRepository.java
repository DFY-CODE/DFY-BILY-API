package one.dfy.bily.api.space.model.repository;

import one.dfy.bily.api.space.model.Amenity;
import one.dfy.bily.api.space.model.SpaceAmenity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpaceAmenityRepository extends JpaRepository<SpaceAmenity, Long> {
}
