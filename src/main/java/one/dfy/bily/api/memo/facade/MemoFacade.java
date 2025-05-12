package one.dfy.bily.api.memo.facade;

import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.common.annotation.Facade;
import one.dfy.bily.api.memo.dto.CreateMemoInfo;
import one.dfy.bily.api.memo.dto.MemoCommonResponse;
import one.dfy.bily.api.memo.dto.MemoInfo;
import one.dfy.bily.api.memo.dto.MemoResponse;
import one.dfy.bily.api.memo.service.MemoService;
import one.dfy.bily.api.user.model.User;
import one.dfy.bily.api.user.service.UserService;

import java.util.List;

@Facade
@RequiredArgsConstructor
public class MemoFacade {
    private final MemoService memoService;
    private final UserService userService;

    public MemoCommonResponse createMemo(CreateMemoInfo createMemoInfo, Long userId) {
        User user = userService.findUserById(userId);

        return memoService.createMemo(createMemoInfo, user);
    }

    public MemoResponse findMemoByUserIdAndInquiryId(Long userId, Long inquiryId) {
        User user = userService.findUserById(userId);

        return memoService.findMemoByUserIdAndInquiryId(user, inquiryId);
    }


}
