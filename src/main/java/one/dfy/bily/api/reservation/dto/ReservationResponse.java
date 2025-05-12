package one.dfy.bily.api.reservation.dto;

public record ReservationResponse (
        Long id,
        Long inquiryId,
        String companyName,
        String hostCompany,
        String eventName,
        String eventCategory,
        String contactPerson,
        String position,
        ReservationInfo reservation,
        String spaceIdName
){
}
