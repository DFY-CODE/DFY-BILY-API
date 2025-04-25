package one.dfy.bily.api.admin.dto.reservation;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import one.dfy.bily.api.admin.constant.ReservationStatus;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
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
