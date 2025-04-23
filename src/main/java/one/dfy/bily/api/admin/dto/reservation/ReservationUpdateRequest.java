package one.dfy.bily.api.admin.dto.reservation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record ReservationUpdateRequest(
        @JsonProperty("id") Long id,
        @JsonProperty("inquiry_id") Long inquiryId,
        @JsonProperty("deposit") PaymentUpdateRequest deposit,
        @JsonProperty("interim_payment1") PaymentUpdateRequest interimPayment1,
        @JsonProperty("interim_payment2") PaymentUpdateRequest interimPayment2,
        @JsonProperty("final_payment") PaymentUpdateRequest finalPayment,
        @JsonProperty("fixed_date") ReservationPreferredDate fixedDate,
        @JsonProperty("status") String status,
        @JsonProperty("created_at") LocalDateTime createdAt
) { }
