package one.dfy.bily.api.admin.dto.reservation;

public record ReservationInfo(
        String status,
        ReservationPreferredDate fixedDate
) {
}
