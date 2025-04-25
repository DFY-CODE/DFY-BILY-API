package one.dfy.bily.api.admin.model.reservation.repository;

import one.dfy.bily.api.admin.model.reservation.Payment;
import one.dfy.bily.api.admin.model.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByReservation(Reservation reservationId);
}
