package one.dfy.bily.api.reservation.mapper;

import one.dfy.bily.api.common.dto.Pagination;
import one.dfy.bily.api.common.mapper.PaginationMapper;
import one.dfy.bily.api.reservation.constant.PaymentType;
import one.dfy.bily.api.inquiry.dto.InquiryFileName;
import one.dfy.bily.api.inquiry.dto.InquiryPreferredDateInfo;
import one.dfy.bily.api.inquiry.model.Inquiry;
import one.dfy.bily.api.reservation.model.Payment;
import one.dfy.bily.api.reservation.model.Reservation;
import one.dfy.bily.api.reservation.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class ReservationMapper {

    public static Reservation toReservationEntity(ReservationPaymentInfo dto, Inquiry inquiry, Long adminId) {

        return new Reservation(
                inquiry,
                dto.status(),
                dto.fixedDate().from(),
                dto.fixedDate().to(),
                inquiry.getUserId(),
                adminId,
                adminId
        );
    }

    public static Reservation toReservationEntity(CreateReservation dto, Inquiry inquiry, Long adminId) {

        return new Reservation(
                inquiry,
                dto.status(),
                dto.fixedDate().from(),
                dto.fixedDate().to(),
                inquiry.getUserId(),
                adminId,
                adminId
        );
    }

    public static List<Payment> toPaymentEntities(ReservationPaymentInfo dto, Reservation reservation) {
        List<Payment> payments = new ArrayList<>();

        payments.add(toPayment(dto.deposit(), PaymentType.DEPOSIT, reservation));
        payments.add(toPayment(dto.interimPayment1(), PaymentType.INTERIM_PAYMENT1, reservation));
        payments.add(toPayment(dto.interimPayment2(), PaymentType.INTERIM_PAYMENT2, reservation));
        payments.add(toPayment(dto.finalPayment(), PaymentType.FINAL_PAYMENT, reservation));

        return payments;
    }

    public static List<Payment> toPaymentEntities(CreateReservation dto, Reservation reservation) {
        List<Payment> payments = new ArrayList<>();

        payments.add(toPayment(dto.deposit(), PaymentType.DEPOSIT, reservation));
        payments.add(toPayment(dto.interimPayment1(), PaymentType.INTERIM_PAYMENT1, reservation));
        payments.add(toPayment(dto.interimPayment2(), PaymentType.INTERIM_PAYMENT2, reservation));
        payments.add(toPayment(dto.finalPayment(), PaymentType.FINAL_PAYMENT, reservation));

        return payments;
    }

    private static Payment toPayment(PaymentRequest request, PaymentType type, Reservation reservation) {

        return new Payment(
                reservation,
                type,
                request.payment(),
                request.date()
        );
    }

    private static Payment toPayment(CreatePaymentRequest request, PaymentType type, Reservation reservation) {

        return new Payment(
                reservation,
                type,
                request.payment(),
                request.date()
        );
    }

    public static ReservationResponse toReservationResponse(
            Reservation reservations
    ) {
        return new ReservationResponse(
                reservations.getId(),
                reservations.getInquiry().getId(),
                reservations.getInquiry().getCompanyName(),
                reservations.getInquiry().getHostCompany(),
                reservations.getInquiry().getEventName(),
                reservations.getInquiry().getEventCategory(),
                reservations.getInquiry().getContactPerson(),
                reservations.getInquiry().getPosition(),
                new ReservationInfo(
                        reservations.getStatus().getDescription(),
                        new ReservationPreferredDateInfo(reservations.getStartDate(), reservations.getEndDate())
                ),
                reservations.getInquiry().getSpace().getAlias()
        );
    }

    public static ReservationDetailResponse toReservationDetailResponse(
            Reservation reservation,
            List<InquiryFileName> files,
            List<InquiryPreferredDateInfo> preferredDateInfos
    ) {

        return new ReservationDetailResponse(
                reservation.getId(),
                reservation.getInquiry().getId(),
                reservation.getInquiry().getContactPerson(),
                reservation.getInquiry().getPhoneNumber(),
                reservation.getInquiry().getEmail(),
                reservation.getInquiry().getCompanyName(),
                reservation.getInquiry().getPosition(),
                reservation.getInquiry().getCompanyWebsite(),
                reservation.getInquiry().getEventCategory(),
                reservation.getInquiry().getEventName(),
                preferredDateInfos,
                reservation.getInquiry().getContent(),
                files,
                reservation.getInquiry().getCreatedAt(),
                reservation.getInquiry().getStatus(),
                reservation.getInquiry().getSpace().getAlias(),
                reservation.getInquiry().getHostCompany(),
                new ReservationInfo(reservation.getStatus().getDescription(), new ReservationPreferredDateInfo(reservation.getStartDate(), reservation.getEndDate()))
        );
    }

    public static ReservationPaymentInfo toReservationPaymentInfo(
            Reservation reservation,
            List<Payment> payments
    ) {
        return new ReservationPaymentInfo(
                reservation.getId(),
                reservation.getInquiry().getId(),
                getPaymentUpdateRequest(payments, PaymentType.DEPOSIT),
                getPaymentUpdateRequest(payments, PaymentType.INTERIM_PAYMENT1),
                getPaymentUpdateRequest(payments, PaymentType.INTERIM_PAYMENT2),
                getPaymentUpdateRequest(payments, PaymentType.FINAL_PAYMENT),
                new ReservationPreferredDateInfo(reservation.getStartDate(), reservation.getEndDate()),
                reservation.getStatus(),
                reservation.getCreatedAt()
        );
    }

    private static PaymentRequest getPaymentUpdateRequest(List<Payment> payments, PaymentType type) {
        return payments.stream()
                .filter(p -> p.isEqualType(type))
                .findFirst()
                .map(p -> new PaymentRequest(p.getId(), p.getPaymentDate(), p.getAmount()))
                .orElse(null);
    }

    public static ReservationListResponse toReservationListResponse(Page<ReservationResponse> reservationResponsePage) {
        Pageable pageable = reservationResponsePage.getPageable();
        Pagination pagination = PaginationMapper.toPagination(pageable, reservationResponsePage.getTotalElements(), reservationResponsePage.getTotalPages());

        return new ReservationListResponse(reservationResponsePage.getContent(), pagination);
    }
}
