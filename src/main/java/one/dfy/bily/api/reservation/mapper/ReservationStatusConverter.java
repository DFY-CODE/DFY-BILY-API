package one.dfy.bily.api.reservation.mapper;

import one.dfy.bily.api.inquiry.constant.InquirySearchType;
import one.dfy.bily.api.reservation.constant.ReservationStatus;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ReservationStatusConverter implements Converter<String, ReservationStatus> {
    @Override
    public ReservationStatus convert(@NotNull String source) {
        return ReservationStatus.from(source);
    }
}
