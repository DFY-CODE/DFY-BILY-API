package one.dfy.bily.api.reservation.dto;

import one.dfy.bily.api.reservation.constant.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationPaymentInfo(
        Long id,
        Long inquiryId,
        PaymentRequest deposit,
        PaymentRequest interimPayment1,
        PaymentRequest interimPayment2,
        PaymentRequest finalPayment,
        ReservationPreferredDateInfo fixedDate,
        ReservationStatus status,
        LocalDateTime createdAt
) { }
