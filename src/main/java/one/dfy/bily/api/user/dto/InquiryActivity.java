package one.dfy.bily.api.user.dto;

import one.dfy.bily.api.inquiry.constant.InquiryStatus;
import one.dfy.bily.api.reservation.constant.ReservationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InquiryActivity(
        Long id,
        Long spaceId,
        String spaceName,
        String location,
        BigDecimal areaM2,
        Integer areaPy,
        Long price,
        InquiryStatus status,
        LocalDateTime createdAt
) {
}
