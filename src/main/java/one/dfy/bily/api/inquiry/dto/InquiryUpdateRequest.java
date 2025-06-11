package one.dfy.bily.api.inquiry.dto;


import java.util.List;

public record InquiryUpdateRequest(

        Long inquiryId,
        String spaceIdName,
        String companyName,
        String companyWebsite,
        String contactPerson,
        String position,
        String email,
        String phoneNumber,
        String hostCompany,
        String eventCategory,
        String content,
        String spaceId,
        List<InquiryPreferredDateInfo> preferredDates
) { }