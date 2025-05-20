package one.dfy.bily.api.inquiry.service;

import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.inquiry.constant.InquirySearchType;
import one.dfy.bily.api.inquiry.constant.InquiryStatus;
import one.dfy.bily.api.inquiry.mapper.InquiryMapper;
import one.dfy.bily.api.inquiry.model.Inquiry;
import one.dfy.bily.api.inquiry.model.InquiryFile;
import one.dfy.bily.api.inquiry.model.PreferredDate;
import one.dfy.bily.api.inquiry.model.repository.InquiryFileRepository;
import one.dfy.bily.api.inquiry.model.repository.PreferredDateRepository;
import one.dfy.bily.api.inquiry.model.repository.InquiryRepository;
import one.dfy.bily.api.user.dto.InquiryActivity;
import one.dfy.bily.api.space.model.Space;
import one.dfy.bily.api.inquiry.dto.*;
import one.dfy.bily.api.util.S3Uploader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final InquiryFileRepository inquiryFileRepository;
    private final PreferredDateRepository preferredDateRepository;
    private final S3Uploader s3Uploader;

    @Transactional(readOnly = true)
    public InquiryListResponse findInquiryListByKeywordAndDate(
            InquirySearchType type, String keyword,
            LocalDateTime startAt, LocalDateTime endAt,
            int page, int pageSize,
            List<InquiryStatus> statusList
    ) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        InquiryKeywordHolder holder = InquiryMapper.mapKeyword(type, keyword);

        Page<InquiryResponse> inquiryResponsePage =  inquiryRepository.searchInquiries(
                holder.companyName(),
                holder.contactPerson(),
                holder.spaceName(),
                startAt,
                endAt,
                pageable,
                statusList
        );

        return InquiryMapper.toInquiryListResponse(inquiryResponsePage);
    }

    @Transactional(readOnly = true)
    public InquiryResponse findInquiryByInquiryIdAndUserId(Long inquiryId, Long userId, boolean isAdmin) {

//        if(isAdmin){
//            return inquiryRepository.findById(inquiryId)
//                    .map(inquiry -> {
//                        List<InquiryFile> files = inquiryFileRepository.findByInquiry(inquiry);
//                        List<PreferredDate> preferredDates = preferredDateRepository.findByInquiry(inquiry);
//
//                        return InquiryMapper.toInquiryResponse(inquiry, files, preferredDates);
//                    })
//                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 문의 정보입니다."));
//        }
//
//        return inquiryRepository.findByIdAndUserId(inquiryId, userId)
//                .map(inquiry -> {
//                    List<InquiryFile> files = inquiryFileRepository.findByInquiry(inquiry);
//                    List<PreferredDate> preferredDates = preferredDateRepository.findByInquiry(inquiry);
//
//                    return InquiryMapper.toInquiryResponse(inquiry, files, preferredDates);
//                })
//                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 문의 정보입니다."));
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
    public InquiryResponse createInquiry(InquiryCreateRequest request, List<MultipartFile> fileAttachments, Space space, Long userId) {

        Inquiry inquiry = InquiryMapper.inquiryCreateRequestToEntity(request, space, userId);
        inquiryRepository.save(inquiry);

        List<PreferredDate> preferredDates = request.preferredDates().stream()
                .map(dto -> InquiryMapper.inquiryPreferredDateInfoToEntity(dto, inquiry))
                .toList();
        preferredDateRepository.saveAll(preferredDates);

        List<InquiryFile> inquiryFiles = fileAttachments.stream()
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

    @Transactional
    public void updateInquiryStatus(Long id, InquiryStatusUpdateRequest request){
        inquiryRepository.findById(id).ifPresent(inquiry -> {
            inquiry.updateStatus(request.status());
        });
    }

    @Transactional(readOnly = true)
    public Map<Long, List<InquiryPreferredDateInfo>> findInquiryPreferredDateByInquiryActivity(List<InquiryActivity> inquiryActivities){

        List<Long> inquiryIds = inquiryActivities.stream()
                .map(InquiryActivity::id)
                .toList();

        List<PreferredDate> preferredDates = preferredDateRepository.findByInquiryIdIn(inquiryIds);
        return InquiryMapper.toPreferredDateMap(preferredDates);
    }

    @Transactional(readOnly = true)
    public Map<Long, List<InquiryPreferredDateInfo>> findInquiryPreferredDateByObject(List<Object[]> rawResults){
        List<Long> inquiryIds = rawResults.stream()
                .filter(r -> "INQUIRY".equals(r[2]))
                .map(r -> ((Number) r[0]).longValue())
                .toList();

        List<PreferredDate> preferredDates = preferredDateRepository.findByInquiryIdIn(inquiryIds);
        return InquiryMapper.toPreferredDateMap(preferredDates);
    }

    @Transactional(readOnly = true)
    public Page<InquiryActivity> findInquiryActivitiesByUserId(Long userId, int page, int pageSize) {

        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        return inquiryRepository.findInquiryActivitiesByUserId(userId, pageable);
    }

    public List<Long> getInquiryActivityInquiryIds(List<InquiryActivity> result) {
        return result.stream()
                .map(InquiryActivity::spaceId)
                .toList();
    }
}