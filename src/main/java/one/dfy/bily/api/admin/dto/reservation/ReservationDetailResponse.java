package one.dfy.bily.api.admin.dto.reservation;

import one.dfy.bily.api.admin.constant.InquiryStatus;
import one.dfy.bily.api.admin.dto.Inquiry.InquiryFileName;
import one.dfy.bily.api.admin.dto.Inquiry.InquiryPreferredDateInfo;
import one.dfy.bily.api.admin.dto.space.SpaceId;

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
        String spaceId,
        String hostCompany,
        SpaceId spaces,
        ReservationInfo reservation
) {
}
