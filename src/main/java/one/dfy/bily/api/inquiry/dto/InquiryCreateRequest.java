package one.dfy.bily.api.inquiry.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import one.dfy.bily.api.inquiry.constant.InquiryStatus;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public record InquiryCreateRequest(
        @JsonProperty("contactPerson") String contactPerson,
        @JsonProperty("phoneNumber") String phoneNumber,
        @JsonProperty("email") String email,
        @JsonProperty("companyName") String companyName,
        @JsonProperty("position") String position,
        @JsonProperty("companyWebsite") String companyWebsite,
        @JsonProperty("eventCategory") String eventCategory,
        @JsonProperty("preferredDates") List<InquiryPreferredDateInfo> preferredDates,
        @JsonProperty("content") String content,
        @JsonProperty("status") InquiryStatus status,
        @JsonProperty("spaceId") String spaceId,
        @JsonProperty("hostCompany") String hostCompany
) {}
