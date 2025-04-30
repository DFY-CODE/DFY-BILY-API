package one.dfy.bily.api.inquiry.dto;

import one.dfy.bily.api.common.dto.Pagination;

import java.util.List;

public record InquiryListResponse(
        List<InquiryResponse> inquiryList,
        Pagination pagination
) {
}
