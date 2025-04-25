package one.dfy.bily.api.admin.dto.Inquiry;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record InquiryCreateRequest(
        String contactPerson,
        String phoneNumber,
        String email,
        String companyName,
        String position,
        String companyWebsite,
        String eventCategory,
        String eventName,
        List<InquiryPreferredDateInfo> preferredDates,
        String content,
        LocalDateTime createdAt,
        String status,
        String author,
        Integer spaceId,
        String hostCompany,
        String spaceIdName,
        List<MultipartFile> fileAttachments
) {}