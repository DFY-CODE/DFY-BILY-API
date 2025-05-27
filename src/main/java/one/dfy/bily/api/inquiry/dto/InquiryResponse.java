package one.dfy.bily.api.inquiry.dto;

import one.dfy.bily.api.inquiry.constant.InquiryStatus;

import java.time.LocalDateTime;
import java.util.List;

public record InquiryResponse(
        Long id,
        String spaceId,
        String contactPerson,
        String phoneNumber,
        String email,
        String companyName,
        String position,
        String companyWebsite,
        String eventCategory,
        List<InquiryPreferredDateInfo> preferredDates,
        String content,
        List<InquiryFileName> fileAttachment,
        LocalDateTime createdAt,
        InquiryStatus status,
        String hostCompany,
        String spaceIdName
) {}