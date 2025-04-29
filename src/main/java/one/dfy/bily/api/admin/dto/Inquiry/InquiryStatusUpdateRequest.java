package one.dfy.bily.api.admin.dto.Inquiry;

import one.dfy.bily.api.admin.constant.InquiryStatus;

public record InquiryStatusUpdateRequest(
        InquiryStatus status
) {
}
