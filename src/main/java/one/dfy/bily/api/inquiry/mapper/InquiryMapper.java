package one.dfy.bily.api.inquiry.mapper;

import one.dfy.bily.api.common.dto.Pagination;
import one.dfy.bily.api.common.mapper.PaginationMapper;
import one.dfy.bily.api.inquiry.constant.InquirySearchType;
import one.dfy.bily.api.reservation.dto.ReservationAndInquiry;
import one.dfy.bily.api.reservation.dto.ReservationPreferredDateInfo;
import one.dfy.bily.api.space.dto.SpaceId;
import one.dfy.bily.api.inquiry.model.Inquiry;
import one.dfy.bily.api.inquiry.model.InquiryFile;
import one.dfy.bily.api.inquiry.model.PreferredDate;
import one.dfy.bily.api.space.model.Space;
import one.dfy.bily.api.inquiry.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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
                request.eventName(),
                request.content(),
                request.status(),
                request.author(),
                space,
                request.hostCompany(),
                userId
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

    public static ReservationAndInquiry toReservationAndInquiryInfo(Object[] row, Map<Long, List<InquiryPreferredDateInfo>> preferredDatesMap) {
        Long id = row[0] != null ? ((Number) row[0]).longValue() : null;
        String type = row[1] != null ? (String) row[1] : null;
        String spaceName = row[2] != null ? (String) row[2] : null;
        String location = row[3] != null ? (String) row[3] : null;
        int areaM2 = row[4] != null ? ((Number) row[4]).intValue() : 0;
        int areaPy = row[5] != null ? ((Number) row[5]).intValue() : 0;
        int maxCapacity = row[6] != null ? ((Number) row[6]).intValue() : 0;
        LocalDateTime from = row[7] != null ? ((Timestamp) row[7]).toLocalDateTime() : null;
        LocalDateTime to = row[8] != null ? ((Timestamp) row[8]).toLocalDateTime() : null;
        Long price = row[9] != null ? ((Number) row[9]).longValue() : 0L;
        String status = row[10] != null ? (String) row[10] : null;
        LocalDateTime createdAt = row[11] != null ? ((Timestamp) row[11]).toLocalDateTime() : null;

        return new ReservationAndInquiry(
                id,
                type,
                spaceName,
                location,
                areaM2,
                areaPy,
                maxCapacity,
                "INQUIRY".equals(type) ? preferredDatesMap.getOrDefault(id, null) : null,
                "RESERVATION".equals(type) ? new ReservationPreferredDateInfo(from, to) : null,
                price,
                status,
                createdAt
        );
    }
}
