package one.dfy.bily.api.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.dfy.bily.api.inquiry.dto.InquiryPreferredDateInfo;
import one.dfy.bily.api.inquiry.mapper.InquiryMapper;
import one.dfy.bily.api.inquiry.model.PreferredDate;
import one.dfy.bily.api.space.model.SavedSpace;
import one.dfy.bily.api.space.model.Space;
import one.dfy.bily.api.user.dto.InquiryActivity;
import one.dfy.bily.api.user.dto.ReservationActivity;
import one.dfy.bily.api.user.dto.SavedSpaceInfo;
import one.dfy.bily.api.user.dto.UserActivity;
import one.dfy.bily.api.user.facade.UserActivityFacade;
import one.dfy.bily.api.user.mapper.UserActivityMapper;
import one.dfy.bily.api.util.AES256Util;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
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
                    log.info(">> Start mapping savedSpace: " + savedSpace);

                    try {
                        Space space = savedSpace.getSpace();
                        if (space == null || space.getId() == null) {
                            throw new IllegalArgumentException("SavedSpace.getSpace() or space ID cannot be null");
                        }

                        Long spaceId = space.getId(); // 공간 ID로 변경
                        log.info("Encrypting Space ID: " + spaceId);

                        String encryptedSpaceId = AES256Util.encrypt(spaceId);
                        return UserActivityMapper.toSavedSpaceInfo(savedSpace, fileNameListMap, encryptedSpaceId);

                    } catch (Exception e) {
                        log.info("Exception: " + e.getMessage());
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }



}
