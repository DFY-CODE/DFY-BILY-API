package one.dfy.bily.api.reservation.model.repository;

import one.dfy.bily.api.reservation.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationCustomRepository {

}
