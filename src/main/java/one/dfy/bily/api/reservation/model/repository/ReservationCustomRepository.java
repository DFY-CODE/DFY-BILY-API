package one.dfy.bily.api.reservation.model.repository;

import one.dfy.bily.api.reservation.dto.ReservationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface ReservationCustomRepository {
    Page<ReservationResponse> findReservationListByKeywordAndDate(
            String companyName,
            String contactPerson,
            String spaceIdKeyword,
            LocalDateTime startAt,
            LocalDateTime endAt,
            Pageable pageable
    );
}
