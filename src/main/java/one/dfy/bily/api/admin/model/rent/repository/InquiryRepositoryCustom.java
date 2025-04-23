package one.dfy.bily.api.admin.model.rent.repository;

import one.dfy.bily.api.admin.dto.Inquiry.InquiryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface InquiryRepositoryCustom {
    Page<InquiryResponse> searchInquiries(String companyName, String contactPerson, String spaceIdKeyword, LocalDateTime startAt, LocalDateTime endAt, Pageable pageable);
}
