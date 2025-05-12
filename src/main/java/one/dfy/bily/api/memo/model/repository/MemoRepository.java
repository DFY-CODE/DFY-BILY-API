package one.dfy.bily.api.memo.model.repository;

import one.dfy.bily.api.memo.model.Memo;
import one.dfy.bily.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemoRepository extends JpaRepository<Memo, Long> {
    List<Memo> findByUserAndInquiryIdAndUsed(User user, Long inquiryId, boolean isUsed);
}
