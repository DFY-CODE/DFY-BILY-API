package one.dfy.bily.api.admin.model.inquiry.repository;

import one.dfy.bily.api.admin.model.inquiry.Inquiry;
import one.dfy.bily.api.admin.model.inquiry.InquiryFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryFileRepository extends JpaRepository<InquiryFile, Long> {
    List<InquiryFile> findByInquiry(Inquiry inquiry);
}
