package one.dfy.bily.api.inquiry.dto;

import one.dfy.bily.api.inquiry.constant.InquiryStatus;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public record InquiryCreateRequest(
        String contactPerson,
        String phoneNumber,
        String email,
        String companyName,
        String position,
        String companyWebsite,
        String eventCategory,
        List<InquiryPreferredDateInfo> preferredDates,
        String content,
        InquiryStatus status,
        Long spaceId,
        String hostCompany,
        String spaceIdName
) {}