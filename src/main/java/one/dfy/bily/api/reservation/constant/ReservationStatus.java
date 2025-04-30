package one.dfy.bily.api.reservation.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum ReservationStatus {
    IN_PROGRESS("계약중"),
    CONTRACT_COMPLETED("계약완료"),
    VENUE_RESERVED("대관완료"),
    CANCELLED("해약");

    private final String description;

    ReservationStatus(String description) {
        this.description = description;
    }

    @JsonCreator
    public static ReservationStatus from(String value) {
        for (ReservationStatus status : values()) {
            if (status.description.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status: " + value);
    }

}
