package one.dfy.bily.api.reservation.dto;

import one.dfy.bily.api.inquiry.constant.InquiryStatus;
import one.dfy.bily.api.inquiry.dto.InquiryFileName;
import one.dfy.bily.api.inquiry.dto.InquiryPreferredDateInfo;

import java.time.LocalDateTime;
import java.util.List;

public record ReservationDetailResponse(
        Long id,
        Long inquiryId,
        String contact_person,
        String phoneNumber,
        String email,
        String companyName,
        String position,
        String companyWebsite,
        String eventCategory,
        String eventName,
        List<InquiryPreferredDateInfo> preferredDates,
        String content,
        List<InquiryFileName> fileAttachment,
        LocalDateTime createdAt,
        InquiryStatus status,
        String spaceIdName,
        String hostCompany,
        ReservationInfo reservation
) {
}
