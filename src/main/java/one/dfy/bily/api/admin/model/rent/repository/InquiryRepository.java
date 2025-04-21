package one.dfy.bily.api.admin.model.rent.repository;

import one.dfy.bily.api.admin.model.rent.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InquiryRepository extends JpaRepository<Inquiry, Long>, InquiryRepositoryCustom {
    Optional<Inquiry> findByInquiryId(long InquiryId);

}
