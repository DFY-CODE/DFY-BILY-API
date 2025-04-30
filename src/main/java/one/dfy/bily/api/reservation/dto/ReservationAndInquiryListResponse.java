package one.dfy.bily.api.reservation.dto;

import one.dfy.bily.api.common.dto.Pagination;

import java.util.List;

public record ReservationAndInquiryListResponse (
        List<ReservationAndInquiry> reservationAndInquiryList,
        Pagination pagination
){

}
