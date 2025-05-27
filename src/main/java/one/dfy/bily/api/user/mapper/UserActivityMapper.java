package one.dfy.bily.api.user.mapper;

import one.dfy.bily.api.inquiry.dto.InquiryPreferredDateInfo;
import one.dfy.bily.api.reservation.dto.ReservationPreferredDateInfo;
import one.dfy.bily.api.space.model.SavedSpace;
import one.dfy.bily.api.space.model.Space;
import one.dfy.bily.api.user.dto.InquiryActivity;
import one.dfy.bily.api.user.dto.ReservationActivity;
import one.dfy.bily.api.user.dto.SavedSpaceInfo;
import one.dfy.bily.api.user.dto.UserActivity;
import one.dfy.bily.api.util.AES256Util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class UserActivityMapper {

    public static UserActivity toReservationAndInquiryInfo(
            Object[] row,
            Map<Long, List<InquiryPreferredDateInfo>> preferredDatesMap,
            Map<Long, String> fileNameListMap
    ) {
        Long id = toLong(row[0]);
        Long spaceIdLong = toLong(row[1]); // 기존 spaceId (Long)
        String spaceId;

        try {
            // AES256Util을 이용해 spaceId 암호화
            spaceId = AES256Util.encrypt(spaceIdLong);
        } catch (Exception e) {
            throw new RuntimeException("SpaceId 암호화 실패", e);
        }

        String type = toStr(row[2]);
        String spaceName = toStr(row[3]);
        String location = toStr(row[4]);
        BigDecimal areaM2 = toBigDecimal(row[5]);
        int areaPy = toPrimitiveInt(row[6]);
        LocalDateTime from = toDateTime(row[8]);
        LocalDateTime to = toDateTime(row[9]);
        Long price = toLong(row[10]);
        String status = toStr(row[11]);
        LocalDateTime createdAt = toDateTime(row[12]);

        return new UserActivity(
                id,
                spaceId, // AES256으로 암호화된 spaceId 전달
                type,
                spaceName,
                location,
                areaM2,
                areaPy,
                "INQUIRY".equals(type) ? preferredDatesMap.getOrDefault(id, null) : null,
                "RESERVATION".equals(type) ? new ReservationPreferredDateInfo(from, to) : null,
                price,
                status,
                createdAt,
                fileNameListMap.getOrDefault(spaceIdLong, null) // 파일 맵은 기존 Long spaceId로 처리
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
            Map<Long, String> fileNameListMap,
            Map<Long, List<InquiryPreferredDateInfo>> preferredDatesMap
    ) {
        String spaceId;
        try {
            // 암호화된 spaceId 생성 (id 값을 암호화)
            spaceId = AES256Util.encrypt(inquiryActivity.id());
        } catch (Exception e) {
            throw new RuntimeException("SpaceId 암호화 실패", e);
        }

        return new UserActivity(
                inquiryActivity.id(),
                spaceId, // 암호화된 spaceId 사용
                "INQUIRY",
                inquiryActivity.spaceName(),
                inquiryActivity.location(),
                inquiryActivity.areaM2(),
                inquiryActivity.areaPy(),
                preferredDatesMap.getOrDefault(inquiryActivity.id(), null),
                null,
                inquiryActivity.price(),
                inquiryActivity.status().getDescription(),
                inquiryActivity.createdAt(),
                fileNameListMap.getOrDefault(inquiryActivity.id(), null) // fileName 맵은 id로 처리
        );
    }





    public static UserActivity fromReservationActivity(
            ReservationActivity reservation,
            Map<Long, String> fileNameListMap
    ) {
        return new UserActivity(
                reservation.id(),
                reservation.spaceId(), // spaceId 추가
                "RESERVATION",
                reservation.spaceName(),
                reservation.location(),
                reservation.areaM2(),
                reservation.areaPy(),
                null, // inquiryPreferredDateList는 예약에서는 없음
                new ReservationPreferredDateInfo(
                        reservation.startDate(),
                        reservation.endDate()
                ),
                reservation.price(),
                reservation.status().getDescription(),
                reservation.createdAt(),
                fileNameListMap.getOrDefault(reservation.spaceId(), null)
        );
    }


    public static SavedSpaceInfo toSavedSpaceInfo(SavedSpace savedSpace, Map<Long, String> fileNameListMap, String encryptedId) {
        try {
            Space space = savedSpace.getSpace();

            if (space == null || space.getId() == null) {
                throw new IllegalArgumentException("SavedSpace.getSpace() or space ID is null");
            }

            return new SavedSpaceInfo(
                    space.getId(),
                    encryptedId,
                    space.getTitle(),
                    space.getDistrictInfo(),
                    space.getAreaM2(),
                    space.getAreaPy(),
                    space.getPrice(),
                    fileNameListMap.getOrDefault(space.getId(), null)
            );
        } catch (Exception e) {
            System.err.println("❌ Error in toSavedSpaceInfo: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


}
