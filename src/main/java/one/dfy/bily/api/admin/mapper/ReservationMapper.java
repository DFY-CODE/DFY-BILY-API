package one.dfy.bily.api.admin.mapper;

import one.dfy.bily.api.admin.constant.PaymentType;
import one.dfy.bily.api.admin.dto.Inquiry.InquiryFile;
import one.dfy.bily.api.admin.dto.Inquiry.InquiryPreferredDate;
import one.dfy.bily.api.admin.dto.reservation.*;
import one.dfy.bily.api.admin.dto.space.SpaceId;
import one.dfy.bily.api.admin.model.rent.Inquiry;
import one.dfy.bily.api.admin.model.reservation.Payment;
import one.dfy.bily.api.admin.model.reservation.Reservation;

import java.util.List;
import java.util.function.Function;

public class ReservationMapper {

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
                        new ReservationPreferredDate(reservations.getStartDate(), reservations.getEndDate())
                ),
                new SpaceId(reservations.getInquiry().getSpace().getSpaceId())
        );
    }

    public static ReservationDetailResponse toReservationDetailResponse(
            Reservation reservation,
            List<InquiryFile> files
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
                new InquiryPreferredDate(reservation.getInquiry().getPreferredStartDate(), reservation.getInquiry().getPreferredEndDate()),
                reservation.getInquiry().getContent(),
                files,
                reservation.getInquiry().getCreatedAt(),
                reservation.getInquiry().getStatus(),
                reservation.getInquiry().getSpace().getSpaceId(),
                reservation.getInquiry().getHostCompany(),
                new SpaceId(reservation.getInquiry().getSpace().getSpaceId()),
                new ReservationInfo(reservation.getStatus().getDescription(), new ReservationPreferredDate(reservation.getStartDate(), reservation.getEndDate()))
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
                new ReservationPreferredDate(reservation.getStartDate(), reservation.getEndDate()),
                reservation.getStatus(),
                reservation.getCreatedAt()
        );
    }

    private static PaymentUpdateRequest getPaymentUpdateRequest(List<Payment> payments, PaymentType type) {
        return payments.stream()
                .filter(p -> p.isEqualType(type))
                .findFirst()
                .map(p -> new PaymentUpdateRequest(p.getId(), p.getPaymentDate(), p.getAmount()))
                .orElse(null);
    }
}
