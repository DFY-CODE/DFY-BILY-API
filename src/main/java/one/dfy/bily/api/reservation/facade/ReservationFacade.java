package one.dfy.bily.api.reservation.facade;

import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.inquiry.dto.InquiryFileName;
import one.dfy.bily.api.inquiry.dto.InquiryPreferredDateInfo;
import one.dfy.bily.api.reservation.dto.CreateReservation;
import one.dfy.bily.api.reservation.dto.ReservationDetailResponse;
import one.dfy.bily.api.reservation.dto.ReservationPaymentInfo;
import one.dfy.bily.api.inquiry.model.Inquiry;
import one.dfy.bily.api.reservation.model.Reservation;
import one.dfy.bily.api.inquiry.service.InquiryService;
import one.dfy.bily.api.reservation.service.ReservationService;
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

    public ReservationPaymentInfo createReservationPayment(CreateReservation request, Long adminId) {
        Inquiry inquiry = inquiryService.findInquiryById(request.inquiryId());

        return reservationService.createReservationPayment(request, inquiry, adminId);
    }

}
