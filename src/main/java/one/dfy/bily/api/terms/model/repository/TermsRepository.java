package one.dfy.bily.api.terms.model.repository;

import one.dfy.bily.api.terms.constant.TermsCode;
import one.dfy.bily.api.terms.model.Terms;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TermsRepository extends JpaRepository<Terms, Long> {
    List<Terms> findByCodeIn(List<TermsCode> codes);
}
