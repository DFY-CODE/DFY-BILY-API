package one.dfy.bily.api.user.service;

import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.inquiry.dto.InquiryPreferredDateInfo;
import one.dfy.bily.api.inquiry.mapper.InquiryMapper;
import one.dfy.bily.api.inquiry.model.PreferredDate;
import one.dfy.bily.api.space.model.SavedSpace;
import one.dfy.bily.api.user.dto.InquiryActivity;
import one.dfy.bily.api.user.dto.ReservationActivity;
import one.dfy.bily.api.user.dto.SavedSpaceInfo;
import one.dfy.bily.api.user.dto.UserActivity;
import one.dfy.bily.api.user.facade.UserActivityFacade;
import one.dfy.bily.api.user.mapper.UserActivityMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserActivityService {

    @Transactional(readOnly = true)
    public List<UserActivity> mappingToReservationAndInquiryInfo(
            List<Object[]> rawResults,
            Map<Long, String> fileNameListMap,
            Map<Long, List<InquiryPreferredDateInfo>> preferredDateMap
    ){

        return rawResults.stream()
                .map(row -> UserActivityMapper.toReservationAndInquiryInfo(row, preferredDateMap, fileNameListMap))
                .toList();
    }

    public List<UserActivity> reservationActivityToUserActivityList(
            List<ReservationActivity> reservations,
            Map<Long, String> fileNameListMap
    ) {
        return reservations.stream()
                .map(reservation -> UserActivityMapper.fromReservationActivity(reservation, fileNameListMap))
                .toList();
    }

    public List<UserActivity> inquiryActivityToUserActivityList(
            List<InquiryActivity> inquiryActivities,
            Map<Long, String> fileNameListMap,
            Map<Long, List<InquiryPreferredDateInfo>> preferredDateMap
    ) {
        return inquiryActivities.stream()
                .map(inquiryActivity -> UserActivityMapper.fromInquiryActivity(inquiryActivity, fileNameListMap,preferredDateMap))
                .toList();
    }

    public List<SavedSpaceInfo> savedSpaceToSavedSpaceList(
            List<SavedSpace> savedSpaces,
            Map<Long, String> fileNameListMap
    ) {
        return savedSpaces.stream()
                .map(savedSpace -> {
                    String encryptedSpaceId = encryptSpaceId(savedSpace.getId()); // spaceId 암호화
                    return UserActivityMapper.toSavedSpaceInfo(savedSpace, fileNameListMap, encryptedSpaceId);
                })
                .toList();
    }

    private String encryptSpaceId(Long spaceId) {
        try {
            // 예제 암호화 로직 (Base64로 변환하는 간단한 방식 사용)
            return Base64.getEncoder().encodeToString(spaceId.toString().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalArgumentException("SpaceId 암호화 실패", e);
        }
    }

}
