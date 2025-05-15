package one.dfy.bily.api.reservation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreatePaymentRequest(
        LocalDateTime date,
        BigDecimal payment
){
}
