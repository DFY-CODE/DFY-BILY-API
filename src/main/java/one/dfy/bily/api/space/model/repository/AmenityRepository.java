package one.dfy.bily.api.space.model.repository;

import one.dfy.bily.api.space.model.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {
    List<Amenity> findByUsed(boolean used);
}
