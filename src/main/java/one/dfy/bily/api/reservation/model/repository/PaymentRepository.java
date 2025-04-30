package one.dfy.bily.api.reservation.model.repository;

import one.dfy.bily.api.reservation.model.Payment;
import one.dfy.bily.api.reservation.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByReservation(Reservation reservationId);
}
