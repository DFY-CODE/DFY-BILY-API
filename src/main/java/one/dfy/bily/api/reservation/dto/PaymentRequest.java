package one.dfy.bily.api.reservation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentRequest(
        Long paymentId,
        LocalDateTime date,
        BigDecimal payment
){
}
