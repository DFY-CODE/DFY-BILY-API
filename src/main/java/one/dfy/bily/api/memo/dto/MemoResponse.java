package one.dfy.bily.api.memo.dto;

import java.util.List;

public record MemoResponse(
        List<MemoInfo> memoInfoList
) {
}
