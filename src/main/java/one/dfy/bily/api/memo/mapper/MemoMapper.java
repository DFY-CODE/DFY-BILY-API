package one.dfy.bily.api.memo.mapper;

import one.dfy.bily.api.memo.dto.CreateMemoInfo;
import one.dfy.bily.api.memo.dto.MemoInfo;
import one.dfy.bily.api.memo.model.Memo;
import one.dfy.bily.api.user.model.User;

public class MemoMapper {
    public static Memo toMemoEntity(CreateMemoInfo createMemoInfo, User user) {
        return new Memo(
                createMemoInfo.type(),
                createMemoInfo.content(),
                user,
                createMemoInfo.inquiryId()
        );
    }

    public static MemoInfo toMemoResponse(Memo memo) {
        return new MemoInfo(
                memo.getId(),
                memo.getContent(),
                memo.getUser().getUserName(),
                memo.getCreatedAt()
        );
    }
}
