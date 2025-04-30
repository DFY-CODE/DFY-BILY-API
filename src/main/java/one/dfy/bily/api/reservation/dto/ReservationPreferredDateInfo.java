package one.dfy.bily.api.reservation.dto;

import java.time.LocalDateTime;

public record ReservationPreferredDateInfo(
        LocalDateTime from,
        LocalDateTime to
) {
}
