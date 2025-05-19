package one.dfy.bily.api.space.model.repository;

import one.dfy.bily.api.space.model.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpaceAmenityRepository extends JpaRepository<Amenity, Long> {
}
