package one.dfy.bily.api.admin.dto.Inquiry;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record InquiryUpdateRequest(
        @JsonProperty("company_name") String companyName,
        @JsonProperty("contact_person") String contactPerson,
        @JsonProperty("position") String position,
        @JsonProperty("email") String email,
        @JsonProperty("phone_number") String phoneNumber,
        @JsonProperty("host_company") String hostCompany,
        @JsonProperty("event_category") String eventCategory,
        @JsonProperty("content") String content,
        @JsonProperty("space_id") Long spaceId,
        @JsonProperty("preferred_dates") List<InquiryPreferredDate> preferredDates
) { }