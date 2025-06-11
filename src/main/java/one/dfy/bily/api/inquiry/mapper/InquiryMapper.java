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
import one.dfy.bily.api.util.AES256Util;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.GeneralSecurityException;
import java.util.Base64;
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

    public static InquiryResponse toInquiryResponseV2(Inquiry inquiry, List<InquiryFile> files, List<PreferredDate> preferredDates,String s3Url) {

        return inquiryToResponseV2(inquiry, files, preferredDates, s3Url);
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
        String encryptedSpaceId;
        try {
            encryptedSpaceId = AES256Util.encrypt(inquiry.getSpace().getId());
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Failed to encrypt space ID", e); // 또는 커스텀 예외로 래핑
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new InquiryResponse(
                inquiry.getId(),
                encryptedSpaceId, // 🔐 암호화된 spaceId
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
                inquiry.getHostCompany(),
                inquiry.getSpace().getAlias(),
                null,
                null,
                null
        );
    }

    private static InquiryResponse inquiryToResponseV2(Inquiry inquiry, List<InquiryFile> inquiryFile, List<PreferredDate> preferredDates, String s3UrlPrefix) {
        List<InquiryFileName> inquiryFileNames = inquiryFile.stream()
                .map(file -> InquiryMapper.inquiryFileV2ToResponse(file, s3UrlPrefix)) // ✅ 두 번째 인자 전달
                .toList();


        List<InquiryPreferredDateInfo> preferredDateInfos = preferredDates.stream()
                .map(InquiryMapper::inquiryPreferredDateInfoToResponse)
                .toList();
        String encryptedSpaceId;
        try {
            encryptedSpaceId = AES256Util.encrypt(inquiry.getSpace().getId());
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Failed to encrypt space ID", e); // 또는 커스텀 예외로 래핑
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new InquiryResponse(
                inquiry.getId(),
                encryptedSpaceId, // 🔐 암호화된 spaceId
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
                inquiry.getHostCompany(),
                inquiry.getSpace().getAlias(),
                null,
                null,
                null
        );
    }

    public class EncryptionUtils {
        public static String encryptId(Long id) {
            // 암호화 로직 예시 (Base64나 AES 등 실제 사용 중인 로직에 맞게 구현)
            return Base64.getEncoder().encodeToString(String.valueOf(id).getBytes());
        }
    }

    public static InquiryFileName inquiryFileV2ToResponse(InquiryFile file, String s3UrlPrefix) {
        return new InquiryFileName(
                file.getId(),
                file.getFileName(),
                s3UrlPrefix + file.getSaveFileName()   // ✅ URL 생성
        );
    }


    public static InquiryFileName inquiryFileToResponse(InquiryFile file) {

        return new InquiryFileName(
                file.getId(),
                file.getFileName(),
                null
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
