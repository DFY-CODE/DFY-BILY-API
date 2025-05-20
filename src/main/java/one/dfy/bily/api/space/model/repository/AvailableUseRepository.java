package one.dfy.bily.api.space.model.repository;

import one.dfy.bily.api.space.model.AvailableUse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvailableUseRepository extends JpaRepository<AvailableUse, Long> {
    List<AvailableUse> findByUsed(boolean used);
}
