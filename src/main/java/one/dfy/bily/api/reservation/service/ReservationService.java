package one.dfy.bily.api.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.dfy.bily.api.inquiry.constant.InquirySearchType;
import one.dfy.bily.api.reservation.constant.PaymentType;
import one.dfy.bily.api.inquiry.dto.InquiryFileName;
import one.dfy.bily.api.inquiry.dto.InquiryKeywordHolder;
import one.dfy.bily.api.inquiry.dto.InquiryPreferredDateInfo;
import one.dfy.bily.api.reservation.constant.ReservationStatus;
import one.dfy.bily.api.reservation.dto.*;
import one.dfy.bily.api.inquiry.mapper.InquiryMapper;
import one.dfy.bily.api.reservation.mapper.ReservationMapper;
import one.dfy.bily.api.inquiry.model.Inquiry;
import one.dfy.bily.api.reservation.model.Payment;
import one.dfy.bily.api.reservation.model.Reservation;
import one.dfy.bily.api.reservation.model.repository.PaymentRepository;
import one.dfy.bily.api.reservation.model.repository.ReservationRepository;
import one.dfy.bily.api.user.dto.ReservationActivity;
import one.dfy.bily.api.user.dto.UserActivity;
import one.dfy.bily.api.user.dto.UserActivityList;
import one.dfy.bily.api.user.facade.UserActivityFacade;
import one.dfy.bily.api.util.AES256Util;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public ReservationListResponse findReservationListByKeywordAndDate(
            InquirySearchType type, String keyword,
            LocalDateTime startAt, LocalDateTime endAt,
            int page, int pageSize,
            List<ReservationStatus> reservationStatusList
    ) {

        Pageable pageable = PageRequest.of(page-1, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        InquiryKeywordHolder holder = InquiryMapper.mapKeyword(type, keyword);

        Page<ReservationResponse> responsePage = reservationRepository.findReservationListByKeywordAndDate(
                holder.companyName(),
                holder.contactPerson(),
                holder.spaceName(),
                startAt,
                endAt,
                pageable,
                reservationStatusList
        );

        return ReservationMapper.toReservationListResponse(responsePage);
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
    public ReservationPaymentInfo createReservationPayment(CreateReservation request, Inquiry inquiry, Long adminId) {
        Reservation reservation = ReservationMapper.toReservationEntity(request, inquiry, adminId);
        reservation = reservationRepository.save(reservation);

        List<Payment> payment = ReservationMapper.toPaymentEntities(request, reservation);
        payment = paymentRepository.saveAll(payment);

        return ReservationMapper.toReservationPaymentInfo(reservation, payment);
    }

    @Transactional
    public ReservationPaymentInfo updateReservation(Long reservationId, ReservationPaymentInfo request, Long adminId) {
        Reservation reservation  = reservationRepository.findById(reservationId).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 예약 정보입니다."));
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

        reservation.updateReservation(request.status(), request.fixedDate().from(), request.fixedDate().to(), adminId);

        return request;
    }

    @Transactional(readOnly = true)
    public List<UserActivity> findReservationAndInquiryRow(Long userId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Object[]> rows = reservationRepository.findReservationAndInquiryRow(userId, pageSize, offset);


        return rows.stream()
                .map(row -> {
                    // 필드 추출 및 안전 변환
                    Long id = (Long) row[0];
                    String spaceId = String.valueOf(row[1]);
                    String type = (String) row[2];
                    String spaceName = (String) row[3];
                    String location = (String) row[4];
                    BigDecimal areaM2 = (BigDecimal) row[5];
                    Integer areaPy = (Integer) row[6];

                    LocalDateTime reservationStart = toLocalDateTimeSafely(row[7]);
                    LocalDateTime reservationEnd = toLocalDateTimeSafely(row[8]);
                    Long price = row[9] instanceof BigDecimal bd ? bd.longValue() : null;
                    String status = (String) row[10];
                    LocalDateTime createdAt = toLocalDateTimeSafely(row[11]);

                    LocalDateTime preferredStart = toLocalDateTimeSafely(row[12]);
                    LocalDateTime preferredEnd = toLocalDateTimeSafely(row[13]);

                    ReservationPreferredDateInfo reservationPreferredDate = null;
                    if (reservationStart != null && reservationEnd != null) {
                        reservationPreferredDate = new ReservationPreferredDateInfo(reservationStart, reservationEnd);
                    }

                    ReservationPreferredDateInfo preferredReservationDate = null;
                    if (preferredStart != null && preferredEnd != null) {
                        preferredReservationDate = new ReservationPreferredDateInfo(preferredStart, preferredEnd);
                    }

                    return new UserActivity(
                            id,
                            spaceId,
                            type,
                            spaceName,
                            location,
                            areaM2,
                            areaPy,
                            null, // inquiryPreferredDateList는 필요 시 추가
                            reservationPreferredDate,
                            preferredReservationDate,
                            price,
                            status,
                            createdAt,
                            null // thumbnailUrl은 null 또는 필요 시 추가
                    );
                })
                .toList();
    }



    private static LocalDateTime toLocalDateTimeSafely(Object obj) {
        return obj instanceof Timestamp ? ((Timestamp) obj).toLocalDateTime() : null;
    }



    @Transactional(readOnly = true)
    public long countReservationAndInquiryRow(Long userId) {
        return reservationRepository.countReservationAndInquiry(userId);
    }

    public List<Long> getInquiryIds(List<Object[]> result) {
        return result.stream()
                .map(r -> ((Number) r[1]).longValue())
                .toList();
    }

    public List<Long> getReservationActivityInquiryIds(List<ReservationActivity> result) {
        return result.stream()
                .map(activity -> Long.valueOf(activity.spaceId()))
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ReservationActivity> findReservationListByUserId(Long userId, int page, int pageSize) {

        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        return reservationRepository.findReservationListByUserId(userId, pageable);
    }


}
