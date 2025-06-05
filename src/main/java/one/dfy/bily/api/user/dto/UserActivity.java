package one.dfy.bily.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import one.dfy.bily.api.inquiry.dto.InquiryPreferredDateInfo;
import one.dfy.bily.api.reservation.dto.ReservationPreferredDateInfo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "User activity information")
public record UserActivity(
        @Schema(description = "Activity ID", example = "22")
        Long id,

        @Schema(description = "Space ID", example = "230")
        String spaceId,

        @Schema(description = "Activity type", example = "RESERVATION or INQUIRY")
        String type,

        @Schema(description = "Name of the space", example = "성수역 도보 3분")
        String spaceName,

        @Schema(description = "Location of the space", example = "서울특별시 강남구 강남대로140길9")
        String location,

        @Schema(description = "Area in square meters", example = "122.00")
        BigDecimal areaM2,

        @Schema(description = "Area in Pyong (Korean real estate unit)", example = "31")
        Integer areaPy,

        @Schema(description = "Preferred inquiry date list", nullable = true)
        List<InquiryPreferredDateInfo> inquiryPreferredDateList,

        @Schema(
                description = "Preferred reservation date",
                nullable = true,
                example = "{\"from\": \"2025-06-06T00:00:00\", \"to\": \"2025-06-07T23:59:59\"}"
        )
        ReservationPreferredDateInfo reservationPreferredDate,

        @Schema(
                description = "Preferred reservation date (from preferred_start_date and preferred_end_date)",
                nullable = true,
                example = "{\"from\": \"2025-06-06T00:00:00\", \"to\": \"2025-06-07T23:59:59\"}"
        )
        ReservationPreferredDateInfo preferredReservationDate, // 추가 필드

        @Schema(description = "Price of the reservation", example = "1000000")
        Long price,

        @Schema(description = "Reservation or inquiry status", example = "CONTRACT_COMPLETED")
        String status,

        @Schema(description = "Creation date", example = "2025-05-29T14:45:30")
        LocalDateTime createdAt,

        @Schema(description = "Thumbnail URL")
        String thumbnailUrl
) {}


