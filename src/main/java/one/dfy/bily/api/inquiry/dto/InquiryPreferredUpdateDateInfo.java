package one.dfy.bily.api.inquiry.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record InquiryPreferredUpdateDateInfo(
        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ssX",   // ← X : +09, Z 등 허용
                timezone = "UTC")
        LocalDateTime from,

        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ssX",
                timezone = "UTC")
        LocalDateTime to,

        Integer preferenceLevel


) {}