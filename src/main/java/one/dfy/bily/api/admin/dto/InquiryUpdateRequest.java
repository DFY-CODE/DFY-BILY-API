package one.dfy.bily.api.admin.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

public record InquiryUpdateRequest(
        String companyName,
        String contactPerson,
        String position,
        String email,
        String phoneNumber,
        String hostCompany,
        String eventCategory,
        String content,
        Long spaceId,
        List<InquiryPreferredDate> preferredDates
) { }