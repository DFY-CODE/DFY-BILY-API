package one.dfy.bily.api.admin.dto.reservation;

import java.time.LocalDateTime;

public record ReservationPreferredDateInfo(
        LocalDateTime from,
        LocalDateTime to
) {
}
