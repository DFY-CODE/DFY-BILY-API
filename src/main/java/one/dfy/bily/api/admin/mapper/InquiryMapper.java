package one.dfy.bily.api.admin.mapper;

import one.dfy.bily.api.admin.dto.InquirySpaces;
import one.dfy.bily.api.admin.dto.InquiryFile;
import one.dfy.bily.api.admin.dto.InquiryPreferredDate;
import one.dfy.bily.api.admin.dto.InquiryResponse;
import one.dfy.bily.api.admin.model.rent.Inquiry;
import one.dfy.bily.api.admin.model.rent.InquiryFileInfo;

import java.util.List;
import java.util.Map;

public class InquiryMapper {

    public static InquiryResponse toInquiryResponse(Inquiry inquiry, Map<Long, List<InquiryFile>> filesByInquiryId) {
        List<InquiryFile> fileNames = filesByInquiryId.getOrDefault(inquiry.getId(), List.of());
        return inquiryToResponse(inquiry, fileNames);
    }

    public static InquiryResponse toInquiryResponse(Inquiry inquiry, List<InquiryFileInfo> files) {
        List<InquiryFile> inquiryFiles = files.stream()
                .map(InquiryMapper::inquiryToResponse)
                .toList();
        return inquiryToResponse(inquiry, inquiryFiles);
    }

    private static InquiryResponse inquiryToResponse(Inquiry inquiry, List<InquiryFile> inquiryFiles) {
        return new InquiryResponse(
                inquiry.getId(),
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
                inquiryFiles,
                inquiry.getCreatedAt(),
                inquiry.getStatus(),
                inquiry.getAuthor(),
                inquiry.getSpace().getContentId(),
                inquiry.getHostCompany(),
                inquiry.getSpace().getSpaceId(),
                new InquirySpaces(inquiry.getSpace().getSpaceId())
        );
    }

    private static InquiryFile inquiryToResponse(InquiryFileInfo file) {
        return new InquiryFile(
                file.getId(),
                file.getFileName()
        );
    }
}
