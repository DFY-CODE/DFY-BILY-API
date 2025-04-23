package one.dfy.bily.api.admin.dto.reservation;

import one.dfy.bily.api.admin.dto.Inquiry.InquiryPreferredDate;
import one.dfy.bily.api.admin.dto.space.SpaceId;

import java.util.List;

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
