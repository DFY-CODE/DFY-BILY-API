package one.dfy.bily.api.user.facade;

import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.common.annotation.Facade;
import one.dfy.bily.api.common.dto.Pagination;
import one.dfy.bily.api.inquiry.dto.InquiryPreferredDateInfo;
import one.dfy.bily.api.inquiry.service.InquiryService;
import one.dfy.bily.api.reservation.dto.ReservationPreferredDateInfo;
import one.dfy.bily.api.space.service.SpaceService;
import one.dfy.bily.api.user.dto.InquiryActivity;
import one.dfy.bily.api.user.dto.ReservationActivity;
import one.dfy.bily.api.user.dto.UserActivity;
import one.dfy.bily.api.user.dto.UserActivityList;
import one.dfy.bily.api.reservation.service.ReservationService;
import one.dfy.bily.api.user.service.UserActivityService;
import org.springframework.data.domain.Page;

import java.util.Collections;
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
        List<Integer> contentIds = reservationService.getInquiryIds(reservationAndInquiryRow);
        long totalCount = reservationService.countReservationAndInquiryRow(userId);

        Map<Integer, List<String>> fileNameListMap = spaceService.findByContentIds(contentIds);

        Map<Long, List<InquiryPreferredDateInfo>> preferredDateMap = inquiryService.findInquiryPreferredDateByInquiryIds(reservationAndInquiryRow, fileNameListMap);
        List<UserActivity> userActivityList = userActivityService.mappingToReservationAndInquiryInfo(reservationAndInquiryRow, fileNameListMap, preferredDateMap);

        Pagination pagination = new Pagination(page, pageSize, totalCount, (int) Math.ceil((double) totalCount / pageSize));

        return new UserActivityList(userActivityList, pagination);
    }

    public UserActivityList findReservationListByUserId(Long userId, int page, int pageSize) {
        Page<ReservationActivity> reservationActivityPage = reservationService.findReservationListByUserId(userId, page, pageSize);
        List<Integer> contentIds = reservationService.getReservationActivityInquiryIds(reservationActivityPage.getContent());

        Map<Integer, List<String>> fileNameListMap = spaceService.findByContentIds(contentIds);

        List<UserActivity> userActivityList = userActivityService.reservationActivityToUserActivityList(reservationActivityPage.getContent(), fileNameListMap);
        Pagination pagination = new Pagination(page, pageSize, reservationActivityPage.getTotalElements(), reservationActivityPage.getTotalPages());

        return new UserActivityList(userActivityList, pagination);
    }

    public UserActivityList findInquiryListByUserId(Long userId, int page, int pageSize) {
        Page<InquiryActivity> inquiryActivityPage = inquiryService.findInquiryActivitiesByUserId(userId, page, pageSize);
        List<Integer> contentIds = inquiryService.getInquiryActivityInquiryIds(inquiryActivityPage.getContent());

        Map<Integer, List<String>> fileNameListMap = spaceService.findByContentIds(contentIds);

        Map<Long, List<InquiryPreferredDateInfo>> preferredDateMap = inquiryService.findInquiryPreferredDateByInquiryIds(inquiryActivityPage.getContent());

        List<UserActivity> userActivityList = userActivityService.inquiryActivityToUserActivityList(inquiryActivityPage.getContent(), fileNameListMap, preferredDateMap);
        Pagination pagination = new Pagination(page, pageSize, inquiryActivityPage.getTotalElements(), inquiryActivityPage.getTotalPages());

        return new UserActivityList(userActivityList, pagination);
    }
}
