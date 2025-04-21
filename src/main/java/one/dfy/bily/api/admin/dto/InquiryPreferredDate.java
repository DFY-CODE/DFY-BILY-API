package one.dfy.bily.api.admin.dto;

import java.time.LocalDateTime;

public record InquiryPreferredDate(
        LocalDateTime from,
        LocalDateTime to
) {}