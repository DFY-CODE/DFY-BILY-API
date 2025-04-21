package one.dfy.bily.api.admin.model.rent.repository;

import one.dfy.bily.api.admin.model.rent.Inquiry;
import one.dfy.bily.api.admin.model.rent.InquiryFileInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InquiryFileInfoRepository extends JpaRepository<InquiryFileInfo, Long> {
    List<InquiryFileInfo> findByInquiry(Inquiry inquiry);
}
