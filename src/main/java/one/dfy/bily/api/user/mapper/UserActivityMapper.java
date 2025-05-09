package one.dfy.bily.api.user.mapper;

import one.dfy.bily.api.inquiry.dto.InquiryPreferredDateInfo;
import one.dfy.bily.api.reservation.dto.ReservationPreferredDateInfo;
import one.dfy.bily.api.space.model.SavedSpace;
import one.dfy.bily.api.space.model.Space;
import one.dfy.bily.api.user.dto.InquiryActivity;
import one.dfy.bily.api.user.dto.ReservationActivity;
import one.dfy.bily.api.user.dto.SavedSpaceInfo;
import one.dfy.bily.api.user.dto.UserActivity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UserActivityMapper {

    public static UserActivity toReservationAndInquiryInfo(
            Object[] row,
            Map<Long, List<InquiryPreferredDateInfo>> preferredDatesMap,
            Map<Integer, String> fileNameListMap
    ) {
        Long id = toLong(row[0]);
        Integer contentId = toInt(row[1]);
        String type = toStr(row[2]);
        String spaceName = toStr(row[3]);
        String location = toStr(row[4]);
        BigDecimal areaM2 = toBigDecimal(row[5]);
        int areaPy = toPrimitiveInt(row[6]);
        int maxCapacity = toPrimitiveInt(row[7]);
        LocalDateTime from = toDateTime(row[8]);
        LocalDateTime to = toDateTime(row[9]);
        Long price = toLong(row[10]);
        String status = toStr(row[11]);
        LocalDateTime createdAt = toDateTime(row[12]);


        return new UserActivity(
                id,
                type,
                spaceName,
                location,
                areaM2,
                areaPy,
                maxCapacity,
                "INQUIRY".equals(type) ? preferredDatesMap.getOrDefault(id, null) : null,
                "RESERVATION".equals(type) ? new ReservationPreferredDateInfo(from, to) : null,
                price,
                status,
                createdAt,
                fileNameListMap.getOrDefault(contentId, null)
        );
    }

    private static String toStr(Object obj) {
        return obj != null ? obj.toString() : null;
    }

    private static Long toLong(Object obj) {
        return obj instanceof Number num ? num.longValue() : null;
    }

    private static Integer toInt(Object obj) {
        return obj instanceof Number num ? num.intValue() : null;
    }

    private static int toPrimitiveInt(Object obj) {
        return obj instanceof Number num ? num.intValue() : 0;
    }

    private static LocalDateTime toDateTime(Object obj) {
        return obj instanceof Timestamp ts ? ts.toLocalDateTime() : null;
    }

    private static BigDecimal toBigDecimal(Object obj) {
        return obj instanceof BigDecimal bd ? bd : null;
    }

    public static UserActivity fromInquiryActivity(
            InquiryActivity inquiryActivity,
            Map<Integer, String> fileNameListMap,
            Map<Long, List<InquiryPreferredDateInfo>> preferredDatesMap
    ) {
        return new UserActivity(
                inquiryActivity.id(),
                "INQUIRY",
                inquiryActivity.spaceName(),
                inquiryActivity.location(),
                inquiryActivity.areaM2(),
                inquiryActivity.areaPy(),
                inquiryActivity.maxCapacity(),
                preferredDatesMap.getOrDefault(inquiryActivity.id(), null),
                null,
                inquiryActivity.price(),
                inquiryActivity.status().getDescription(),
                inquiryActivity.createdAt(),
                fileNameListMap.getOrDefault(inquiryActivity.contentId(), null)
        );
    }



    public static UserActivity fromReservationActivity(
            ReservationActivity reservation,
            Map<Integer, String> fileNameListMap
    ) {
        return new UserActivity(
                reservation.id(),
                "RESERVATION",
                reservation.spaceName(),
                reservation.location(),
                reservation.areaM2(),
                reservation.areaPy(),
                reservation.maxCapacity(),
                null, // inquiryPreferredDateList는 예약에서는 없음
                new ReservationPreferredDateInfo(
                        reservation.startDate(),
                        reservation.endDate()
                ),
                reservation.price(),
                reservation.status().getDescription(),
                reservation.createdAt(),
                fileNameListMap.getOrDefault(reservation.contentId(), null)
        );
    }

    public static SavedSpaceInfo toSavedSpaceInfo(SavedSpace savedSpace, Map<Integer, String> fileNameListMap) {
        Space space = savedSpace.getSpace();
        return new SavedSpaceInfo(
                space.getContentId(),
                space.getName(),
                space.getLocation(),
                space.getAreaM2(),
                space.getAreaPy(),
                space.getPrice(),
                space.getTags(),
                fileNameListMap.getOrDefault(space.getContentId(), null)
        );
    }

}
