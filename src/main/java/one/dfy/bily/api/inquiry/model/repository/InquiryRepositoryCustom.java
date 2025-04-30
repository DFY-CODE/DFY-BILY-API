package one.dfy.bily.api.inquiry.model.repository;

import one.dfy.bily.api.inquiry.dto.InquiryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface InquiryRepositoryCustom {
    Page<InquiryResponse> searchInquiries(String companyName, String contactPerson, String spaceIdKeyword, LocalDateTime startAt, LocalDateTime endAt, Pageable pageable);
}
