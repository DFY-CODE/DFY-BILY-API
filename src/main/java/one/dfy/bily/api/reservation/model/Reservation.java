package one.dfy.bily.api.reservation.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.dfy.bily.api.reservation.constant.ReservationStatus;
import one.dfy.bily.api.inquiry.model.Inquiry;
import one.dfy.bily.api.common.constant.YesNo;
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

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "IS_USE", nullable = false)
    private YesNo isUse;

    public Reservation(Inquiry inquiry, ReservationStatus status, LocalDateTime startDate, LocalDateTime endDate) {
        this.inquiry = inquiry;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isUse = YesNo.Y;
    }

    public void updateReservation(ReservationStatus status, LocalDateTime startDate, LocalDateTime endDate) {
        if (status != null) {
            this.status = status;
        }
        if (startDate != null) {
            this.startDate = startDate;
        }
        if (endDate != null) {
            this.endDate = endDate;
        }
    }

    public void deleteReservation() {
        this.isUse = YesNo.N;
    }
}
