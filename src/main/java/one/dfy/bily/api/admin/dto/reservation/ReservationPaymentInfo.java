package one.dfy.bily.api.admin.dto.reservation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import one.dfy.bily.api.admin.constant.ReservationStatus;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ReservationPaymentInfo(
       Long id,
        Long inquiryId,
        PaymentUpdateRequest deposit,
        PaymentUpdateRequest interimPayment1,
        PaymentUpdateRequest interimPayment2,
        PaymentUpdateRequest finalPayment,
        ReservationPreferredDateInfo fixedDate,
        ReservationStatus status,
        LocalDateTime createdAt
) { }
