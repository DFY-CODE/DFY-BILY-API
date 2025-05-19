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
        List<Object[]> reservationAndInquiryRow = reservationService.findReservationAndInquiryRow(userId, page, pageSize);
        List<Long> contentIds = reservationService.getInquiryIds(reservationAndInquiryRow);
        long totalCount = reservationService.countReservationAndInquiryRow(userId);

        Map<Long, String> fileNameListMap = spaceService.findSpaceFileBySpaceIds(contentIds);

        Map<Long, List<InquiryPreferredDateInfo>> preferredDateMap = inquiryService.findInquiryPreferredDateByObject(reservationAndInquiryRow);
        List<UserActivity> userActivityList = userActivityService.mappingToReservationAndInquiryInfo(reservationAndInquiryRow, fileNameListMap, preferredDateMap);

        Pagination pagination = new Pagination(page + 1, pageSize, totalCount, (int) Math.ceil((double) totalCount / pageSize));

        return new UserActivityList(userActivityList, pagination);
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
