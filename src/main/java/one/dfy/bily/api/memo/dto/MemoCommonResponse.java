package one.dfy.bily.api.memo.dto;

public record MemoCommonResponse(
        Long id,
        boolean isSuccess,
        String message
) {
}
