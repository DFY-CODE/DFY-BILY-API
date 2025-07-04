package one.dfy.bily.api.reservation.dto;

public record ReservationResponse (
        Long id,
        String spaceId,
        Long inquiryId,
        String companyName,
        String hostCompany,
        String eventCategory,
        String contactPerson,
        String position,
        ReservationInfo reservation,
        String spaceIdName
){
}
