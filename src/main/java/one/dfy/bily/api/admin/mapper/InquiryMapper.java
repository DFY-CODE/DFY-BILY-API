package one.dfy.bily.api.admin.mapper;

import one.dfy.bily.api.admin.dto.InquerySpaces;
import one.dfy.bily.api.admin.dto.InquiryPreferredDate;
import one.dfy.bily.api.admin.dto.InquiryResponse;
import one.dfy.bily.api.admin.model.rent.Inquiry;
import one.dfy.bily.api.admin.model.rent.InquiryFileInfo;

import java.util.List;
import java.util.Map;

public class InquiryMapper {

    public static InquiryResponse toInquiryResponse(Inquiry inquiry, Map<Long, List<String>> filesByInquiryId) {
        List<String> fileNames = filesByInquiryId.getOrDefault(inquiry.getInquiryId(), List.of());
        return mapToResponse(inquiry, fileNames);
    }

    public static InquiryResponse toInquiryResponse(Inquiry inquiry, List<InquiryFileInfo> files) {
        List<String> fileNames = files.stream()
                .map(InquiryFileInfo::getFileName)
                .toList();
        return mapToResponse(inquiry, fileNames);
    }

    private static InquiryResponse mapToResponse(Inquiry inquiry, List<String> fileNames) {
        return new InquiryResponse(
                inquiry.getInquiryId(),
                inquiry.getContactPerson(),
                inquiry.getPhoneNumber(),
                inquiry.getEmail(),
                inquiry.getCompanyName(),
                inquiry.getPosition(),
                inquiry.getCompanyWebsite(),
                inquiry.getEventCategory(),
                inquiry.getEventName(),
                List.of(new InquiryPreferredDate(
                        inquiry.getPreferredStartDate(),
                        inquiry.getPreferredEndDate()
                )),
                inquiry.getContent(),
                fileNames,
                inquiry.getCreatedAt(),
                inquiry.getStatus(),
                inquiry.getAuthor(),
                inquiry.getSpace().getContentId(),
                inquiry.getHostCompany(),
                inquiry.getSpace().getSpaceId(),
                new InquerySpaces(inquiry.getSpace().getSpaceId())
        );
    }
}
