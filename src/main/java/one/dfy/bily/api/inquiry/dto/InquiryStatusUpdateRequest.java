package one.dfy.bily.api.inquiry.dto;

import one.dfy.bily.api.inquiry.constant.InquiryStatus;

public record InquiryStatusUpdateRequest(
        InquiryStatus status
) {
}
