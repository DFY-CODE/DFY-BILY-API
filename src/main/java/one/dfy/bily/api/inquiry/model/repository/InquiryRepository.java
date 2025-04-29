package one.dfy.bily.api.inquiry.model.repository;

import one.dfy.bily.api.inquiry.model.Inquiry;
import one.dfy.bily.api.common.constant.YesNo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InquiryRepository extends JpaRepository<Inquiry, Long>, InquiryRepositoryCustom {
    Optional<Inquiry> findByIdAndIsUse(long id, YesNo isUse);

}
