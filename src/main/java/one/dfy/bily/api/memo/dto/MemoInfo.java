package one.dfy.bily.api.memo.dto;

import java.time.LocalDateTime;

public record MemoInfo(
        Long id,
        String content,
        String userName,
        LocalDateTime createdAt
) {
}
