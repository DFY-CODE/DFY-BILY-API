package one.dfy.bily.api.admin.service;

import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.admin.constant.InquirySearchType;
import one.dfy.bily.api.admin.constant.PaymentType;
import one.dfy.bily.api.admin.dto.Inquiry.InquiryFileName;
import one.dfy.bily.api.admin.dto.Inquiry.InquiryPreferredDateInfo;
import one.dfy.bily.api.admin.dto.reservation.ReservationDetailResponse;
import one.dfy.bily.api.admin.dto.reservation.ReservationResponse;
import one.dfy.bily.api.admin.dto.reservation.ReservationPaymentInfo;
import one.dfy.bily.api.admin.mapper.ReservationMapper;
import one.dfy.bily.api.admin.model.reservation.Payment;
import one.dfy.bily.api.admin.model.reservation.Reservation;
import one.dfy.bily.api.admin.model.reservation.repository.PaymentRepository;
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

    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

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
        return reservationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 예약 정보입니다."));
    }

    public ReservationDetailResponse reservationMappingToReservationDetailResponse(Reservation reservation, List<InquiryFileName> inquiryFilelistName, List<InquiryPreferredDateInfo> preferredDateInfos) {
        return ReservationMapper.toReservationDetailResponse(reservation, inquiryFilelistName, preferredDateInfos);
    }

    @Transactional
    public ReservationPaymentInfo findReservationPaymentById(Long id) {
        Reservation reservation  = reservationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 예약 정보입니다."));
        List<Payment> paymentList = paymentRepository.findByReservation(reservation);

        return ReservationMapper.toReservationPaymentInfo(reservation, paymentList);
    }

    @Transactional
    public ReservationPaymentInfo updateReservation(ReservationPaymentInfo request) {
        Reservation reservation  = reservationRepository.findById(request.id()).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 예약 정보입니다."));
        List<Payment> paymentList = paymentRepository.findByReservation(reservation);

        paymentList.forEach(payment -> {
            if(payment.isEqualType(PaymentType.DEPOSIT)){
                payment.updatePayment(request.deposit().date(),request.deposit().payment());
            }
            if(payment.isEqualType(PaymentType.INTERIM_PAYMENT1)){
                payment.updatePayment(request.interimPayment1().date(),request.interimPayment1().payment());

            }
            if(payment.isEqualType(PaymentType.INTERIM_PAYMENT2)){
                payment.updatePayment(request.interimPayment2().date(),request.interimPayment2().payment());

            }
            if(payment.isEqualType(PaymentType.FINAL_PAYMENT)){
                payment.updatePayment(request.finalPayment().date(),request.finalPayment().payment());

            }
        });

        reservation.updateReservation(request.status(), request.fixedDate().from(), request.fixedDate().to());

        return request;
    }
}
