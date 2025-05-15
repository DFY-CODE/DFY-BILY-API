package one.dfy.bily.api.reservation.dto;

import one.dfy.bily.api.reservation.constant.ReservationStatus;

import java.time.LocalDateTime;

public record CreateReservation(
        Long inquiryId,
        CreatePaymentRequest deposit,
        CreatePaymentRequest interimPayment1,
        CreatePaymentRequest interimPayment2,
        CreatePaymentRequest finalPayment,
        ReservationPreferredDateInfo fixedDate,
        ReservationStatus status
) { }
