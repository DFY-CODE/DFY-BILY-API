package one.dfy.bily.api.user.facade;

import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.common.annotation.Facade;
import one.dfy.bily.api.common.dto.Pagination;
import one.dfy.bily.api.common.mapper.PaginationMapper;
import one.dfy.bily.api.inquiry.dto.InquiryPreferredDateInfo;
import one.dfy.bily.api.inquiry.service.InquiryService;
import one.dfy.bily.api.space.model.SavedSpace;
import one.dfy.bily.api.space.service.SpaceService;
import one.dfy.bily.api.user.dto.*;
import one.dfy.bily.api.reservation.service.ReservationService;
import one.dfy.bily.api.user.service.UserActivityService;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

@Facade
@RequiredArgsConstructor
public class UserActivityFacade {

    private final ReservationService reservationService;
    private final InquiryService inquiryService;
    private final SpaceService spaceService;
    private final UserActivityService userActivityService;

    public UserActivityList findReservationAndInquiryListByUserId(Long userId, int page, int pageSize) {
        // 1. reservationService 호출
        List<UserActivity> userActivityList = reservationService.findReservationAndInquiryRow(userId, page, pageSize);

        // 2. contentIds 추출
        List<Long> contentIds = userActivityList.stream()
                .map(UserActivity::id)
                .toList();

        // 3. Space 파일 정보 매핑
        Map<Long, String> fileNameListMap = spaceService.findSpaceFileBySpaceIds(contentIds);

        // 4. InquiryPreferredDateInfo 매핑
        Map<Long, List<InquiryPreferredDateInfo>> preferredDateMap =
                inquiryService.findInquiryPreferredDateByObject(userActivityList);

        // 5. 매핑 로직
        List<UserActivity> finalUserActivityList = userActivityService.mappingToReservationAndInquiryInfo(
                userActivityList,
                fileNameListMap,
                preferredDateMap
        );

        // 6. Pagination 생성
        long totalCount = reservationService.countReservationAndInquiryRow(userId);
        Pagination pagination = new Pagination(page, pageSize, totalCount, (int) Math.ceil((double) totalCount / pageSize));

        // 7. 결과 반환
        return new UserActivityList(finalUserActivityList, pagination);
    }



    public UserActivityList findReservationListByUserId(Long userId, int page, int pageSize) {
        Page<ReservationActivity> reservationActivityPage = reservationService.findReservationListByUserId(userId, page, pageSize);
        List<Long> contentIds = reservationService.getReservationActivityInquiryIds(reservationActivityPage.getContent());

        Map<Long, String> fileNameListMap = spaceService.findSpaceFileBySpaceIds(contentIds);

        List<UserActivity> userActivityList = userActivityService.reservationActivityToUserActivityList(reservationActivityPage.getContent(), fileNameListMap);
        Pagination pagination = PaginationMapper.toPagination(reservationActivityPage.getPageable(), reservationActivityPage.getTotalElements(), reservationActivityPage.getTotalPages());

        return new UserActivityList(userActivityList, pagination);
    }

    public UserActivityList findInquiryListByUserId(Long userId, int page, int pageSize) {
        Page<InquiryActivity> inquiryActivityPage = inquiryService.findInquiryActivitiesByUserId(userId, page, pageSize);
        List<Long> spaceIds = inquiryService.getInquiryActivityInquiryIds(inquiryActivityPage.getContent());

        Map<Long, String> fileNameListMap = spaceService.findSpaceFileBySpaceIds(spaceIds);

        Map<Long, List<InquiryPreferredDateInfo>> preferredDateMap = inquiryService.findInquiryPreferredDateByInquiryActivity(inquiryActivityPage.getContent());

        List<UserActivity> userActivityList = userActivityService.inquiryActivityToUserActivityList(inquiryActivityPage.getContent(), fileNameListMap, preferredDateMap);
        Pagination pagination = PaginationMapper.toPagination(inquiryActivityPage.getPageable(), inquiryActivityPage.getTotalElements(), inquiryActivityPage.getTotalPages());

        return new UserActivityList(userActivityList, pagination);
    }

    public SavedSpaceList findSavedSpaceByUserId(Long userId, int page, int pageSize) {
        Page<SavedSpace> spacePage = spaceService.findSavedSpaceByUserId(userId, page, pageSize);
        Map<Long, String> spaceThumbnail = spaceService.findSpaceFileBySpaceList(spacePage.getContent());

        List<SavedSpaceInfo> savedSpaceInfoList = userActivityService.savedSpaceToSavedSpaceList(spacePage.getContent(), spaceThumbnail);

        Pagination pagination = PaginationMapper.toPagination(spacePage.getPageable(), spacePage.getTotalElements(), spacePage.getTotalPages());

        return new SavedSpaceList(savedSpaceInfoList, pagination);
    }
}
