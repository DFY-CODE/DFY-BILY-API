package one.dfy.bily.api.memo.service;

import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.memo.dto.MemoCommonResponse;
import one.dfy.bily.api.memo.dto.CreateMemoInfo;
import one.dfy.bily.api.memo.dto.MemoInfo;
import one.dfy.bily.api.memo.dto.MemoResponse;
import one.dfy.bily.api.memo.mapper.MemoMapper;
import one.dfy.bily.api.memo.model.Memo;
import one.dfy.bily.api.memo.model.repository.MemoRepository;
import one.dfy.bily.api.user.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemoService {
    private final MemoRepository memoRepository;

    @Transactional
    public MemoCommonResponse createMemo(CreateMemoInfo createMemoInfo, User user) {
        Memo memo = MemoMapper.toMemoEntity(createMemoInfo, user);
        memo = memoRepository.save(memo);
        return new MemoCommonResponse(memo.getId(), true, "매모를 생성했습니다.");
    }

    @Transactional(readOnly = true)
    public MemoResponse findMemoByUserIdAndInquiryId(User user, Long inquiryId) {

        List<MemoInfo> memoInfoList = memoRepository.findByUserAndInquiryIdAndUsed(user, inquiryId, true).stream()
                .map(MemoMapper::toMemoResponse).collect(Collectors.toList());
        return new MemoResponse(memoInfoList);
    }

    @Transactional
    public MemoCommonResponse deleteMemo(Long memoId){
        Memo memo = memoRepository.findById(memoId).orElseThrow(()-> new IllegalArgumentException("유효하지 않은 메모입니다."));
        memo.deleteMemo();
        return new MemoCommonResponse(memo.getId(), true, "매모를 삭제했습니다.");
    }
}
