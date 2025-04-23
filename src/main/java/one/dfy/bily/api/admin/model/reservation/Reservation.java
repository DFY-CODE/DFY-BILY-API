package one.dfy.bily.api.admin.model.reservation;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.dfy.bily.api.admin.model.rent.Inquiry;
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
    @JoinColumn(name = "INQUIRY_ID", referencedColumnName = "CONTENT_ID", nullable = false)
    private Inquiry inquiry;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    public void updateReservation(String status, LocalDateTime startDate, LocalDateTime endDate) {
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
