package one.dfy.bily.api.user.dto;

import one.dfy.bily.api.inquiry.dto.InquiryPreferredDateInfo;
import one.dfy.bily.api.reservation.dto.ReservationPreferredDateInfo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record UserActivity(
        Long id,
        String spaceId,
        String type,
        String spaceName,
        String location,
        BigDecimal areaM2,
        int areaPy,
        List<InquiryPreferredDateInfo> inquiryPreferredDateList,
        ReservationPreferredDateInfo reservationPreferredDate,
        Long price,
        String status,
        LocalDateTime createdAt,
        String thumbnailUrl
){
}
