package one.dfy.bily.api.admin.model.reservation.repository;

import one.dfy.bily.api.admin.model.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationCustomRepository {

}
