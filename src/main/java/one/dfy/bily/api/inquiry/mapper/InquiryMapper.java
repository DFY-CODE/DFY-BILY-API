package one.dfy.bily.api.inquiry.mapper;

import one.dfy.bily.api.common.dto.FileUploadInfo;
import one.dfy.bily.api.common.dto.Pagination;
import one.dfy.bily.api.common.mapper.PaginationMapper;
import one.dfy.bily.api.inquiry.constant.InquirySearchType;
import one.dfy.bily.api.inquiry.model.Inquiry;
import one.dfy.bily.api.inquiry.model.InquiryFile;
import one.dfy.bily.api.inquiry.model.PreferredDate;
import one.dfy.bily.api.space.model.Space;
import one.dfy.bily.api.inquiry.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InquiryMapper {

    public static Inquiry inquiryCreateRequestToEntity(InquiryCreateRequest request, Space space, Long userId) {
        return new Inquiry(
                request.contactPerson(),
                request.phoneNumber(),
                request.email(),
                request.companyName(),
                request.position(),
                request.companyWebsite(),
                request.eventCategory(),
                request.content(),
                request.status(),
                space,
                request.hostCompany(),
                userId
        );
    }

    public static InquiryFile inquiryFileToEntity(FileUploadInfo file, Inquiry inquiry, Long userId) {
        return new InquiryFile(
                inquiry,
                file.originalFileName(),
                file.newFileName(),
                file.saveLocation(),
                file.fileSize(),
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
                preferredDateInfos,
                inquiry.getContent(),
                inquiryFileNames,
                inquiry.getCreatedAt(),
                inquiry.getStatus(),
                inquiry.getSpace().getId(),
                inquiry.getHostCompany(),
                inquiry.getSpace().getAlias()
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

    private static InquiryKeywordHolder resolveKeyword(InquirySearchType type, String keyword) {
        if (type == null || keyword == null) {
            return new InquiryKeywordHolder(null, null, null);
        }

        return switch (type) {
            case COMPANY_NAME -> new InquiryKeywordHolder(keyword, null, null);
            case CONTACT_PERSON -> new InquiryKeywordHolder(null, keyword, null);
            case SPACE -> new InquiryKeywordHolder(null, null, keyword);
        };
    }

    public static InquiryKeywordHolder mapKeyword(InquirySearchType type, String keyword) {
        return resolveKeyword(type, keyword);
    }

    public static InquiryListResponse toInquiryListResponse(Page<InquiryResponse> inquiryResponsePage) {
        Pageable pageable = inquiryResponsePage.getPageable();
        Pagination pagination = PaginationMapper.toPagination(pageable, inquiryResponsePage.getTotalElements(), inquiryResponsePage.getTotalPages());

        return new InquiryListResponse(inquiryResponsePage.getContent(),pagination);

    }

    public static Map<Long, List<InquiryPreferredDateInfo>> toPreferredDateMap(List<PreferredDate> preferredDates) {
        return preferredDates.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getInquiry().getId(),
                        Collectors.mapping(
                                p -> new InquiryPreferredDateInfo(
                                        p.getPreferredStartDate(),
                                        p.getPreferredEndDate(),
                                        p.getPreferenceLevel()
                                ),
                                Collectors.toList()
                        )
                ));
    }

}
