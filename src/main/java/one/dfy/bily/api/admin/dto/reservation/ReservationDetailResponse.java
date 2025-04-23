package one.dfy.bily.api.admin.dto.reservation;

import one.dfy.bily.api.admin.dto.Inquiry.InquiryFile;
import one.dfy.bily.api.admin.dto.Inquiry.InquiryPreferredDate;
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
        InquiryPreferredDate preferredDates,
        String content,
        List<InquiryFile> fileAttachment,
        LocalDateTime createdAt,
        String status,
        String spaceId,
        String hostCompany,
        SpaceId spaces,
        ReservationInfo reservation
) {
}
