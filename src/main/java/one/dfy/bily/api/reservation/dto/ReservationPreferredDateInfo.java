package one.dfy.bily.api.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Reservation preferred date information")
public record ReservationPreferredDateInfo(
        @Schema(description = "Reservation start date", example = "2025-06-06T00:00:00")
        LocalDateTime from,
        @Schema(description = "Reservation end date", example = "2025-06-07T23:59:59")
        LocalDateTime to
) {}

