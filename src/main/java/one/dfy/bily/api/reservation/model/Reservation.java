package one.dfy.bily.api.reservation.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.dfy.bily.api.reservation.constant.ReservationStatus;
import one.dfy.bily.api.inquiry.model.Inquiry;
import one.dfy.bily.api.common.model.BaseEntity;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "TBL_RESERVATION")
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INQUIRY_ID", referencedColumnName = "ID", nullable = false)
    private Inquiry inquiry;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private ReservationStatus status;

    @Column(name = "START_DATE")
    private LocalDateTime startDate;

    @Column(name = "END_DATE")
    private LocalDateTime endDate;

    @Column(name = "IS_USED", nullable = false)
    private boolean used;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "CREATOR")
    private Long creator;

    @Column(name = "UPDATER")
    private Long updater;

    public Reservation(Inquiry inquiry, ReservationStatus status, LocalDateTime startDate, LocalDateTime endDate, Long userId, Long creator, Long updater) {
        this.inquiry = inquiry;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.used = true;
        this.userId = userId;
        this.creator = creator;
        this.updater = updater;
    }

    public void updateReservation(ReservationStatus status, LocalDateTime startDate, LocalDateTime endDate, Long updater) {
        if (status != null) {
            this.status = status;
        }
        if (startDate != null) {
            this.startDate = startDate;
        }
        if (endDate != null) {
            this.endDate = endDate;
        }
        this.updater = updater;
    }

    public void deleteReservation() {
        this.used = true;
    }
}
