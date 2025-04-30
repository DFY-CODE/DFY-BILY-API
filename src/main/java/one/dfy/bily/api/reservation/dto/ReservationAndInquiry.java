package one.dfy.bily.api.reservation.dto;

import one.dfy.bily.api.inquiry.dto.InquiryPreferredDateInfo;

import java.time.LocalDateTime;
import java.util.List;

public record ReservationAndInquiry (
        Long id,
        String type,
        String spaceName,
        String location,
        int areaM2,
        int areaPy,
        int maxCapacity,
        List<InquiryPreferredDateInfo> inquiryPreferredDateList,
        ReservationPreferredDateInfo reservationPreferredDate,
        Long price,
        String status,
        LocalDateTime createdAt
){
}
