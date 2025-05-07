package one.dfy.bily.api.user.facade;

import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.common.annotation.Facade;
import one.dfy.bily.api.common.dto.Pagination;
import one.dfy.bily.api.inquiry.service.InquiryService;
import one.dfy.bily.api.space.service.SpaceService;
import one.dfy.bily.api.user.dto.UserActivity;
import one.dfy.bily.api.user.dto.UserActivityList;
import one.dfy.bily.api.reservation.service.ReservationService;

import java.util.List;
import java.util.Map;

@Facade
@RequiredArgsConstructor
public class UserActivityFacade {

    private final ReservationService reservationService;
    private final InquiryService inquiryService;
    private final SpaceService spaceService;

    public UserActivityList findReservationAndInquiryListByUserId(Long userId, int page, int pageSize) {
        List<Object[]> reservationAndInquiryRow = reservationService.findReservationAndInquiryRow(userId, page, pageSize);
        List<Long> contentIds = reservationService.getInquiryIds(reservationAndInquiryRow);
        long totalCount = reservationService.countReservationAndInquiryRow(userId);

        Map<Integer, List<String>> fileNameListMap = spaceService.findByContentIds(contentIds);

        List<UserActivity> userActivityList = inquiryService.mappingToReservationAndInquiryInfo(reservationAndInquiryRow, fileNameListMap);
        Pagination pagination = new Pagination(page, pageSize, totalCount, (int) Math.ceil((double) totalCount / pageSize));

        return new UserActivityList(userActivityList, pagination);
    }
}
