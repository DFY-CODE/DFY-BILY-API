package one.dfy.bily.api.memo.dto;

import one.dfy.bily.api.memo.constant.MemoType;

public record CreateMemoInfo(
        MemoType type,
        String content,
        Long inquiryId
) {
}
