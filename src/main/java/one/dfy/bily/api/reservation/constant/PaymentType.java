package one.dfy.bily.api.reservation.constant;

public enum PaymentType {
    DEPOSIT("보증금"),
    INTERIM_PAYMENT1("중도금1"),
    INTERIM_PAYMENT2("중도금2"),
    FINAL_PAYMENT("잔금")
    ;

    private final String description;

    PaymentType(String description) {
        this.description = description;
    }
}
