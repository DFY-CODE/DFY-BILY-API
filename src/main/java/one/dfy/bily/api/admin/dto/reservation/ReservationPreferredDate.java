package one.dfy.bily.api.admin.dto.reservation;

import java.time.LocalDateTime;

public record ReservationPreferredDate(
        LocalDateTime from,
        LocalDateTime to
) {
}
