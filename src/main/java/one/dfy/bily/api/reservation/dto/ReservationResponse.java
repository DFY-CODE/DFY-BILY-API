package one.dfy.bily.api.reservation.dto;

import one.dfy.bily.api.space.dto.SpaceId;

public record ReservationResponse (
        Long id,
        Long inquiryId,
        String companyName,
        String hostCompany,
        String eventName,
        String eventCategory,
        String contactPerson,
        String position,
        ReservationInfo reservations,
        SpaceId spaces

){
}
