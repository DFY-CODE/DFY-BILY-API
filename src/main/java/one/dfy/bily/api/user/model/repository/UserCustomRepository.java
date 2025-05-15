package one.dfy.bily.api.user.model.repository;

import one.dfy.bily.api.user.constant.UserSearchDateType;
import one.dfy.bily.api.user.dto.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface UserCustomRepository {
    Page<UserInfo> findUsersWithRecentLogin(String email, UserSearchDateType userSearchDateType, LocalDate date, Boolean isUsed, Pageable pageable);
}
