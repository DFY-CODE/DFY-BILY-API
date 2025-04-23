package one.dfy.bily.api.admin.model.reservation;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.dfy.bily.api.admin.constant.PaymentType;
import one.dfy.bily.api.common.model.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE")
    private PaymentType type;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Column(name = "PAYMENT_DATE")
    private LocalDateTime paymentDate;

    public boolean isEqualType(PaymentType type) {
        return this.type == type;
    }

    public void updatePayment(LocalDateTime paymentDate, BigDecimal amount) {
        if (amount != null) {
            this.amount = amount;
        }
        if (paymentDate != null) {
            this.paymentDate = paymentDate;
        }
    }
}
