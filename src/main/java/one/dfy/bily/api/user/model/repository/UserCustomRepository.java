package one.dfy.bily.api.user.model.repository;

import one.dfy.bily.api.user.constant.UserSearchDateType;
import one.dfy.bily.api.user.dto.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface UserCustomRepository {
    Page<UserInfo> findUsersWithRecentLogin(String email, UserSearchDateType userSearchDateType, LocalDateTime startAt, LocalDateTime endAt, Boolean isUsed, Pageable pageable);
}
