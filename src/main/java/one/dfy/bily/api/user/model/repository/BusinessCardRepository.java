package one.dfy.bily.api.user.model.repository;

import one.dfy.bily.api.user.model.BusinessCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusinessCardRepository extends JpaRepository<BusinessCard, Long> {
    Optional<BusinessCard> findByUserIdAndUsed(Long userId, boolean used);
}
