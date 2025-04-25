package one.dfy.bily.api.admin.dto.reservation;

import one.dfy.bily.api.admin.dto.space.SpaceId;

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
