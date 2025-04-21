package one.dfy.bily.api.admin.model.rent.repository;

import one.dfy.bily.api.admin.dto.InquiryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InquiryRepositoryCustom {
    Page<InquiryResponse> searchInquiries(String companyName,  String contactPerson, String spaceIdKeyword, Pageable pageable);
}
