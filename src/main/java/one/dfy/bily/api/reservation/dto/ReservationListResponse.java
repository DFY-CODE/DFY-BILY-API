package one.dfy.bily.api.reservation.dto;

import one.dfy.bily.api.common.dto.Pagination;

import java.util.List;

public record ReservationListResponse(
        List<ReservationResponse> reservationResponseList,
        Pagination pagination
) {
}
