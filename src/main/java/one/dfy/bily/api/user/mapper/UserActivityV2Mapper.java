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

public class UserActivityV2Mapper {

    public static UserActivity toReservationAndInquiryInfo(
            UserActivity activity,
            Map<Long, List<InquiryPreferredDateInfo>> preferredDatesMap,
            Map<Long, String> fileNameListMap
    ) {
        // preferredDateMap와 fileNameListMap 정보 업데이트
        List<InquiryPreferredDateInfo> updatedPreferredDates =
                "INQUIRY".equals(activity.type())
                        ? preferredDatesMap.getOrDefault(activity.id(), null)
                        : null;

        String updatedThumbnail = fileNameListMap.getOrDefault(Long.valueOf(activity.spaceId()), null);

        // 3. spaceId(숫자) → 암호화
        String encryptedSpaceId;
        try {
            long numericSpaceId = Long.parseLong(activity.spaceId()); // int 로 저장돼 있다면 Integer.parseInt 로 변경
            encryptedSpaceId = AES256Util.encrypt(numericSpaceId);
        } catch (Exception e) {
            throw new RuntimeException("spaceId 암호화 실패", e);
        }


        // 새로운 UserActivity 생성 및 기존 필드를 복사 + preferredReservationDate 추가
        return new UserActivity(
                activity.id(),
                encryptedSpaceId,
                activity.type(),
                activity.spaceName(),
                activity.location(),
                activity.areaM2(),
                activity.areaPy(),
                updatedPreferredDates,
                activity.reservationPreferredDate(),
                activity.preferredReservationDate(), // ✅ 추가된 필드 복사
                activity.price(),
                activity.status(),
                activity.createdAt(),
                updatedThumbnail
        );
    }
}
