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

    public static UserActivity fromInquiryActivity(
            InquiryActivity inquiryActivity,
            Map<Long, String> fileNameListMap,
            Map<Long, List<InquiryPreferredDateInfo>> preferredDatesMap
    ) {
        // 공간 ID 가져오기
        Long spaceIdRaw = inquiryActivity.spaceId();   // ← spaceId 를 제공하는 getter 사용

        // spaceId 암호화
        String encryptedSpaceId;
        try {
            encryptedSpaceId = AES256Util.encrypt(spaceIdRaw);
        } catch (Exception e) {
            throw new RuntimeException("SpaceId 암호화 실패", e);
        }

        return new UserActivity(
                inquiryActivity.id(),
                encryptedSpaceId,
                "INQUIRY",
                inquiryActivity.spaceName(),
                inquiryActivity.location(),
                inquiryActivity.areaM2(),
                inquiryActivity.areaPy(),
                preferredDatesMap.getOrDefault(inquiryActivity.id(), null),
                null,
                null,
                inquiryActivity.price(),
                inquiryActivity.status().getDescription(),
                inquiryActivity.createdAt(),
                fileNameListMap.getOrDefault(spaceIdRaw, null) // ★ spaceId 로 조회
        );
    }






    public static UserActivity fromReservationActivity(
            ReservationActivity reservation,
            Map<Long, String> fileNameListMap
    ) {
        ReservationPreferredDateInfo reservationDateInfo = new ReservationPreferredDateInfo(
                reservation.startDate(),
                reservation.endDate()
        );

        return new UserActivity(
                reservation.id(),
                reservation.spaceId(),
                "RESERVATION",
                reservation.spaceName(),
                reservation.location(),
                reservation.areaM2(),
                reservation.areaPy(),
                null,
                reservationDateInfo,        // 예약 실제 사용 날짜
                reservationDateInfo,        // ✅ preferred 예약 날짜도 동일하게 설정
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
