package one.dfy.bily.api.inquiry.mapper;

import one.dfy.bily.api.common.dto.Pagination;
import one.dfy.bily.api.common.mapper.PaginationMapper;
import one.dfy.bily.api.inquiry.constant.InquirySearchType;
import one.dfy.bily.api.user.dto.UserActivity;
import one.dfy.bily.api.reservation.dto.ReservationPreferredDateInfo;
import one.dfy.bily.api.space.dto.SpaceId;
import one.dfy.bily.api.inquiry.model.Inquiry;
import one.dfy.bily.api.inquiry.model.InquiryFile;
import one.dfy.bily.api.inquiry.model.PreferredDate;
import one.dfy.bily.api.space.model.Space;
import one.dfy.bily.api.inquiry.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
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

    public static UserActivity toReservationAndInquiryInfo(
            Object[] row,
            Map<Long, List<InquiryPreferredDateInfo>> preferredDatesMap,
            Map<Integer, List<String>> fileNameListMap
    ) {
        Long id = toLong(row[0]);
        Integer contentId = toInt(row[1]);
        String type = toStr(row[2]);
        String spaceName = toStr(row[3]);
        String location = toStr(row[4]);
        BigDecimal areaM2 = toBigDecimal(row[5]);
        int areaPy = toPrimitiveInt(row[6]);
        int maxCapacity = toPrimitiveInt(row[7]);
        LocalDateTime from = toDateTime(row[8]);
        LocalDateTime to = toDateTime(row[9]);
        Long price = toLong(row[10]);
        String status = toStr(row[11]);
        LocalDateTime createdAt = toDateTime(row[12]);


        return new UserActivity(
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
                createdAt,
                fileNameListMap.getOrDefault(contentId, null)
        );
    }

    private static String toStr(Object obj) {
        return obj != null ? obj.toString() : null;
    }

    private static Long toLong(Object obj) {
        return obj instanceof Number num ? num.longValue() : null;
    }

    private static Integer toInt(Object obj) {
        return obj instanceof Number num ? num.intValue() : null;
    }

    private static int toPrimitiveInt(Object obj) {
        return obj instanceof Number num ? num.intValue() : 0;
    }

    private static LocalDateTime toDateTime(Object obj) {
        return obj instanceof Timestamp ts ? ts.toLocalDateTime() : null;
    }

    private static BigDecimal toBigDecimal(Object obj) {
        return obj instanceof BigDecimal bd ? bd : null;
    }
}
