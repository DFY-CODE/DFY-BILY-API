package one.dfy.bily.api.terms.model.repository;

import one.dfy.bily.api.terms.model.UserTermsAgreement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTermsAgreementRepository extends JpaRepository<UserTermsAgreement, Long> {
}
