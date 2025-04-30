package one.dfy.bily.api.inquiry.model.repository;

import one.dfy.bily.api.inquiry.model.Inquiry;
import one.dfy.bily.api.inquiry.model.InquiryFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryFileRepository extends JpaRepository<InquiryFile, Long> {
    List<InquiryFile> findByInquiry(Inquiry inquiry);
}
