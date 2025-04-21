package one.dfy.bily.api.admin.service;

import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.admin.constant.InquirySearchType;
import one.dfy.bily.api.admin.dto.InquiryResponse;
import one.dfy.bily.api.admin.dto.InquiryUpdateRequest;
import one.dfy.bily.api.admin.mapper.InquiryMapper;
import one.dfy.bily.api.admin.model.rent.Inquiry;
import one.dfy.bily.api.admin.model.rent.InquiryFileInfo;
import one.dfy.bily.api.admin.model.rent.repository.InquiryFileInfoRepository;
import one.dfy.bily.api.admin.model.rent.repository.InquiryRepository;
import one.dfy.bily.api.admin.model.space.Space;
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
    private final InquiryFileInfoRepository inquiryFileInfoRepository;

    @Transactional(readOnly = true)
    public List<InquiryResponse> findInquiryListByKeywordAndDate(InquirySearchType type, String keyword, LocalDateTime startAt, LocalDateTime endAt, int page, int pageSize) {

        Pageable pageable = PageRequest.of(page-1, pageSize, Sort.by(Sort.Direction.DESC, "inquiryId"));

        return inquiryRepository.searchInquiries(
                type == InquirySearchType.COMPANY_NAME ? keyword : null,
                type == InquirySearchType.CONTACT_PERSON ? keyword : null,
                type == InquirySearchType.SPACE ? keyword : null,
                startAt,
                endAt,
                pageable
        ).getContent();
    }

    @Transactional(readOnly = true)
    public InquiryResponse findInquiryByInquiryId(Long inquiryId){

        return inquiryRepository.findById(inquiryId)
                .map(inquiry -> {
                    List<InquiryFileInfo> files = inquiryFileInfoRepository.findByInquiry(inquiry);

                    return InquiryMapper.toInquiryResponse(inquiry, files);
                })
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 문의 정보입니다."));

    }

    @Transactional
    public InquiryResponse updateInquiry(Long inquiryId, InquiryUpdateRequest inquiryUpdateRequest, Space space){

        Inquiry inquiryInfo = inquiryRepository.findById(inquiryId)
                .map(inquiry -> {
                    inquiry.updateFrom(inquiryUpdateRequest, space);
                    return inquiry;
                }).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 문의 정보입니다."));


        List<InquiryFileInfo> files = inquiryFileInfoRepository.findByInquiry(inquiryInfo);

        return InquiryMapper.toInquiryResponse(inquiryInfo, files);

    }
}