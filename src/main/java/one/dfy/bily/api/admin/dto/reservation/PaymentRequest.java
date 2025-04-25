package one.dfy.bily.api.admin.dto.reservation;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PaymentRequest(
        Long paymentId,
        LocalDateTime date,
        BigDecimal payment
){
}
