package one.dfy.bily.api.inquiry.dto;


import java.util.List;

public record InquiryUpdateRequest(
        String companyName,
        String companyWebsite,
        String contactPerson,
        String position,
        String email,
        String phoneNumber,
        String hostCompany,
        String eventCategory,
        String content,
        Integer spaceId,
        List<InquiryPreferredDateInfo> preferredDates
) { }