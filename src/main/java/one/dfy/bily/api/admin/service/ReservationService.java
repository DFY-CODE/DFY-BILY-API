package one.dfy.bily.api.admin.service;

import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.admin.constant.InquirySearchType;
import one.dfy.bily.api.admin.dto.Inquiry.InquiryFile;
import one.dfy.bily.api.admin.dto.reservation.ReservationDetailResponse;
import one.dfy.bily.api.admin.dto.reservation.ReservationResponse;
import one.dfy.bily.api.admin.mapper.ReservationMapper;
import one.dfy.bily.api.admin.model.reservation.Reservation;
import one.dfy.bily.api.admin.model.reservation.repository.ReservationRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private ReservationRepository reservationRepository;

    @Transactional(readOnly = true)
    public List<ReservationResponse> findReservationListByKeywordAndDate(InquirySearchType type, String keyword, LocalDateTime startAt, LocalDateTime endAt, int page, int pageSize) {

        Pageable pageable = PageRequest.of(page-1, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        return reservationRepository.findReservationListByKeywordAndDate(
                type == InquirySearchType.COMPANY_NAME ? keyword : null,
                type == InquirySearchType.CONTACT_PERSON ? keyword : null,
                type == InquirySearchType.SPACE ? keyword : null,
                startAt,
                endAt,
                pageable
        ).getContent();
    }

    @Transactional(readOnly = true)
    public Reservation findReservationDetailById(Long id) {
        return reservationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 문의 정보입니다."));
    }

    public ReservationDetailResponse reservationMappingToReservationDetailResponse(Reservation reservation, List<InquiryFile> inquiryFilelist) {
        return ReservationMapper.toReservationDetailResponse(reservation, inquiryFilelist);
    }
}
