package one.dfy.bily.api.inquiry.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record InquiryUpdateRequest(
        String companyName,
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