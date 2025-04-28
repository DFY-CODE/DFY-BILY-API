package one.dfy.bily.api.admin.service;

import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.admin.constant.InquirySearchType;
import one.dfy.bily.api.admin.dto.Inquiry.*;
import one.dfy.bily.api.admin.mapper.InquiryMapper;
import one.dfy.bily.api.admin.model.inquiry.Inquiry;
import one.dfy.bily.api.admin.model.inquiry.InquiryFile;
import one.dfy.bily.api.admin.model.inquiry.PreferredDate;
import one.dfy.bily.api.admin.model.inquiry.repository.InquiryFileRepository;
import one.dfy.bily.api.admin.model.inquiry.repository.PreferredDateRepository;
import one.dfy.bily.api.admin.model.inquiry.repository.InquiryRepository;
import one.dfy.bily.api.admin.model.space.Space;
import one.dfy.bily.api.util.S3Uploader;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final InquiryFileRepository inquiryFileRepository;
    private final PreferredDateRepository preferredDateRepository;
    private final S3Uploader s3Uploader;

    @Transactional(readOnly = true)
    public List<InquiryResponse> findInquiryListByKeywordAndDate(
            InquirySearchType type, String keyword,
            LocalDateTime startAt, LocalDateTime endAt,
            int page, int pageSize
    ) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        InquiryKeywordHolder holder = InquiryMapper.mapKeyword(type, keyword);

        return inquiryRepository.searchInquiries(
                holder.companyName(),
                holder.contactPerson(),
                holder.spaceName(),
                startAt,
                endAt,
                pageable
        ).getContent();
    }

    @Transactional(readOnly = true)
    public InquiryResponse findInquiryByInquiryId(Long inquiryId){

        return inquiryRepository.findById(inquiryId)
                .map(inquiry -> {
                    List<InquiryFile> files = inquiryFileRepository.findByInquiry(inquiry);
                    List<PreferredDate> preferredDates = preferredDateRepository.findByInquiry(inquiry);

                    return InquiryMapper.toInquiryResponse(inquiry, files, preferredDates);
                })
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 문의 정보입니다."));

    }

    @Transactional(readOnly = true)
    public Inquiry findInquiryById(Long inquiryId){

        return inquiryRepository.findById(inquiryId).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 문의 정보입니다."));

    }

    @Transactional
    public InquiryResponse createInquiry(InquiryCreateRequest request, Space space, Long userId) {

        Inquiry inquiry = InquiryMapper.inquiryCreateRequestToEntity(request, space);
        inquiryRepository.save(inquiry);

        List<PreferredDate> preferredDates = request.preferredDates().stream()
                .map(dto -> InquiryMapper.inquiryPreferredDateInfoToEntity(dto, inquiry))
                .toList();
        preferredDateRepository.saveAll(preferredDates);

        List<InquiryFile> inquiryFiles = request.fileAttachments().stream()
                .map(s3Uploader::inquiryFileUpload)
                .toList()
                .stream()
                .map(file -> InquiryMapper.inquiryFileToEntity(file, inquiry, userId))
                .toList();
        inquiryFileRepository.saveAll(inquiryFiles);

        return InquiryMapper.toInquiryResponse(inquiry, inquiryFiles, preferredDates);
    }

    @Transactional
    public InquiryResponse updateInquiry(Long inquiryId, InquiryUpdateRequest inquiryUpdateRequest, Space space){

        Inquiry inquiryInfo = inquiryRepository.findById(inquiryId)
                .map(inquiry -> {
                    inquiry.updateFrom(inquiryUpdateRequest, space);
                    return inquiry;
                }).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 문의 정보입니다."));

        List<PreferredDate> preferredDates = preferredDateRepository.findByInquiry(inquiryInfo);

        for (PreferredDate entity : preferredDates) {
            inquiryUpdateRequest.preferredDates().stream()
                    .filter(entity::isSameLevel)
                    .findFirst()
                    .ifPresent(entity::updateFromDto);
        }


        List<InquiryFile> files = inquiryFileRepository.findByInquiry(inquiryInfo);

        return InquiryMapper.toInquiryResponse(inquiryInfo, files, preferredDates);

    }

    @Transactional(readOnly = true)
    public List<InquiryFileName> findInquiryFileByInquiry(Inquiry inquiry){
        return inquiryFileRepository.findByInquiry(inquiry).stream()
                .map(InquiryMapper::inquiryFileToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InquiryPreferredDateInfo> findInquiryPreferredDateByInquiry(Inquiry inquiry){
        return preferredDateRepository.findByInquiry(inquiry).stream()
                .map(InquiryMapper::inquiryPreferredDateInfoToResponse)
                .toList();
    }
}