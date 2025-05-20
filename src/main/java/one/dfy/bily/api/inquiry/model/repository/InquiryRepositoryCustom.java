package one.dfy.bily.api.inquiry.model.repository;

import one.dfy.bily.api.inquiry.constant.InquiryStatus;
import one.dfy.bily.api.inquiry.dto.InquiryResponse;
import one.dfy.bily.api.user.dto.InquiryActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface InquiryRepositoryCustom {
    Page<InquiryResponse> searchInquiries(String companyName, String contactPerson, String spaceIdKeyword, LocalDateTime startAt, LocalDateTime endAt, Pageable pageable, List<InquiryStatus> statusList);
    Page<InquiryActivity> findInquiryActivitiesByUserId(Long userId, Pageable pageable);
}
