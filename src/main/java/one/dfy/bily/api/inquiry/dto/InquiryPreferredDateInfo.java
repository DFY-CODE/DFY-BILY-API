package one.dfy.bily.api.inquiry.dto;

import java.time.LocalDateTime;

public record InquiryPreferredDateInfo(
        LocalDateTime from,
        LocalDateTime to,
        Integer preferenceLevel
) {}