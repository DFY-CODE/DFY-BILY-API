package one.dfy.bily.api.admin.dto.reservation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentUpdateRequest (
        @JsonProperty("payment_id")
        Long paymentId,
        LocalDateTime date,
        BigDecimal payment
){
}
