package one.dfy.bily.api.admin.dto.Inquiry;

import java.time.LocalDateTime;

public record InquiryPreferredDateInfo(
        LocalDateTime from,
        LocalDateTime to,
        Integer preferenceLevel
) {}