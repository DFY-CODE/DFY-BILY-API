package one.dfy.bily.api.user.dto;

import one.dfy.bily.api.reservation.constant.ReservationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReservationActivity(
        Long id,
        Integer contentId,
        String spaceName,
        String location,
        BigDecimal areaM2,
        Integer areaPy,
        Integer maxCapacity,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Long price,
        ReservationStatus status,
        LocalDateTime createdAt
) {
}
