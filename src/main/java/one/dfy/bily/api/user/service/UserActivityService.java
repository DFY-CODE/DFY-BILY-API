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

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserActivityService {

    @Transactional(readOnly = true)
    public List<UserActivity> mappingToReservationAndInquiryInfo(
            List<Object[]> rawResults,
            Map<Integer, String> fileNameListMap,
            Map<Long, List<InquiryPreferredDateInfo>> preferredDateMap
    ){

        return rawResults.stream()
                .map(row -> UserActivityMapper.toReservationAndInquiryInfo(row, preferredDateMap, fileNameListMap))
                .toList();
    }

    public List<UserActivity> reservationActivityToUserActivityList(
            List<ReservationActivity> reservations,
            Map<Integer, String> fileNameListMap
    ) {
        return reservations.stream()
                .map(reservation -> UserActivityMapper.fromReservationActivity(reservation, fileNameListMap))
                .toList();
    }

    public List<UserActivity> inquiryActivityToUserActivityList(
            List<InquiryActivity> inquiryActivities,
            Map<Integer, String> fileNameListMap,
            Map<Long, List<InquiryPreferredDateInfo>> preferredDateMap
    ) {
        return inquiryActivities.stream()
                .map(inquiryActivity -> UserActivityMapper.fromInquiryActivity(inquiryActivity, fileNameListMap,preferredDateMap))
                .toList();
    }

    public List<SavedSpaceInfo> savedSpaceToSavedSpaceList(
            List<SavedSpace> savedSpaces,
            Map<Integer, String> fileNameListMap
    ) {
        return savedSpaces.stream()
                .map(savedSpace -> UserActivityMapper.toSavedSpaceInfo(savedSpace,fileNameListMap))
                .toList();
    }
}
