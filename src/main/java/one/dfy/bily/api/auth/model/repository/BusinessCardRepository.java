package one.dfy.bily.api.auth.model.repository;

import one.dfy.bily.api.auth.model.BusinessCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessCardRepository extends JpaRepository<BusinessCard, Long> {
}
