package one.dfy.bily.api.payment.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.dfy.bily.api.admin.model.reservation.Reservation;
import one.dfy.bily.api.common.model.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "TBL_PAYMENT")
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESERVATION_ID", referencedColumnName = "ID", nullable = false)
    private Reservation reservation;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "AMOUNT")
    private Long amount;

}
