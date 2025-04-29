package one.dfy.bily.api.reservation.dto;

public record ReservationInfo(
        String status,
        ReservationPreferredDateInfo fixedDate
) {
}
