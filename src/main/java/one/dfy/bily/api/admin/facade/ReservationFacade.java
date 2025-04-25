package one.dfy.bily.api.admin.facade;

import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.admin.dto.Inquiry.InquiryFileName;
import one.dfy.bily.api.admin.dto.Inquiry.InquiryPreferredDateInfo;
import one.dfy.bily.api.admin.dto.reservation.ReservationDetailResponse;
import one.dfy.bily.api.admin.dto.reservation.ReservationPaymentInfo;
import one.dfy.bily.api.admin.model.inquiry.Inquiry;
import one.dfy.bily.api.admin.model.reservation.Reservation;
import one.dfy.bily.api.admin.service.InquiryService;
import one.dfy.bily.api.admin.service.ReservationService;
import one.dfy.bily.api.common.annotation.Facade;

import java.util.List;

@Facade
@RequiredArgsConstructor
public class ReservationFacade {
    private final ReservationService reservationService;
    private final InquiryService inquiryService;

    public ReservationDetailResponse findReservationDetail(Long id){
        Reservation reservation = reservationService.findReservationDetailById(id);
        List<InquiryFileName> inquiryFileNameList =  inquiryService.findInquiryFileByInquiry(reservation.getInquiry());
        List<InquiryPreferredDateInfo> preferredDateInfos = inquiryService.findInquiryPreferredDateByInquiry(reservation.getInquiry());

        return reservationService.reservationMappingToReservationDetailResponse(reservation, inquiryFileNameList, preferredDateInfos);
    }

    public ReservationPaymentInfo createReservationPayment(ReservationPaymentInfo request) {
        Inquiry inquiry = inquiryService.findInquiryById(request.inquiryId());

        return reservationService.createReservationPayment(request, inquiry);
    }

}
