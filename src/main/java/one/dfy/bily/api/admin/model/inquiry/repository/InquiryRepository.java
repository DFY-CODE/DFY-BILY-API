package one.dfy.bily.api.admin.model.inquiry.repository;

import one.dfy.bily.api.admin.model.inquiry.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InquiryRepository extends JpaRepository<Inquiry, Long>, InquiryRepositoryCustom {
    Optional<Inquiry> findById(long id);

}
