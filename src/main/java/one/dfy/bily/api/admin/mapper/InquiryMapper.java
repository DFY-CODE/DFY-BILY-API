package one.dfy.bily.api.admin.mapper;

import one.dfy.bily.api.admin.dto.Inquiry.*;
import one.dfy.bily.api.admin.dto.space.SpaceId;
import one.dfy.bily.api.admin.model.inquiry.Inquiry;
import one.dfy.bily.api.admin.model.inquiry.InquiryFile;
import one.dfy.bily.api.admin.model.inquiry.PreferredDate;
import one.dfy.bily.api.admin.model.space.Space;

import java.util.List;
import java.util.Map;

public class InquiryMapper {

    public static Inquiry inquiryCreateRequestToEntity(InquiryCreateRequest request, Space space) {
        return new Inquiry(
                request.contactPerson(),
                request.phoneNumber(),
                request.email(),
                request.companyName(),
                request.position(),
                request.companyWebsite(),
                request.eventCategory(),
                request.eventName(),
                request.content(),
                request.status(),
                request.author(),
                space,
                request.hostCompany()
        );
    }

    public static InquiryFile inquiryFileToEntity(InquiryFileDetail file, Inquiry inquiry, Long userId) {
        return new InquiryFile(
                inquiry,
                file.originalFileName(),
                file.newFileName(),
                file.saveLocation(),
                file.fileSize(),
                "N",
                userId,
                userId,
                file.fileType()
        );
    }

    public static PreferredDate inquiryPreferredDateInfoToEntity(InquiryPreferredDateInfo dto, Inquiry inquiry) {
        return new PreferredDate(
                inquiry,
                dto.preferenceLevel(),
                dto.from(),
                dto.to()
        );
    }

    public static InquiryResponse toInquiryResponse(Inquiry inquiry, Map<Long, List<InquiryFile>> filesByInquiryId, Map<Long, List<PreferredDate>> preferredDatesByInquiryId) {
        List<InquiryFile> fileNames = filesByInquiryId.getOrDefault(inquiry.getId(), List.of());
        List<PreferredDate> preferredDateInfos = preferredDatesByInquiryId.getOrDefault(inquiry.getId(), List.of());
        return inquiryToResponse(inquiry, fileNames, preferredDateInfos);
    }

    public static InquiryResponse toInquiryResponse(Inquiry inquiry, List<InquiryFile> files, List<PreferredDate> preferredDates) {

        return inquiryToResponse(inquiry, files, preferredDates);
    }

    private static InquiryResponse inquiryToResponse(Inquiry inquiry, List<InquiryFile> inquiryFile, List<PreferredDate> preferredDates) {
        List<InquiryFileName> inquiryFileNames = inquiryFile.stream()
                .map(InquiryMapper::inquiryFileToResponse)
                .toList();

        List<InquiryPreferredDateInfo> preferredDateInfos = preferredDates.stream()
                .map(InquiryMapper::inquiryPreferredDateInfoToResponse)
                .toList();

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
                preferredDateInfos,
                inquiry.getContent(),
                inquiryFileNames,
                inquiry.getCreatedAt(),
                inquiry.getStatus(),
                inquiry.getAuthor(),
                inquiry.getSpace().getContentId(),
                inquiry.getHostCompany(),
                inquiry.getSpace().getSpaceId(),
                new SpaceId(inquiry.getSpace().getSpaceId())
        );
    }

    public static InquiryFileName inquiryFileToResponse(InquiryFile file) {
        return new InquiryFileName(
                file.getId(),
                file.getFileName()
        );
    }

    public static InquiryPreferredDateInfo inquiryPreferredDateInfoToResponse(PreferredDate preferredDate) {
        return  new InquiryPreferredDateInfo(preferredDate.getPreferredStartDate(), preferredDate.getPreferredEndDate(), preferredDate.getPreferenceLevel());
    }
}
