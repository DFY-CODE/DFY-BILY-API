package one.dfy.bily.api.admin.dto;

import java.time.LocalDateTime;
import java.util.List;

public record InquiryResponse(
        Long id,
        String contactPerson,
        String phoneNumber,
        String email,
        String companyName,
        String position,
        String companyWebsite,
        String eventCategory,
        String eventName,
        List<InquiryPreferredDate> preferredDates,
        String content,
        List<String> fileAttachment,
        LocalDateTime createdAt,
        String status,
        String author,
        Integer spaceId,
        String hostCompany,
        String spaceIdName,
        InquerySpaces inquerySpaces
) {}