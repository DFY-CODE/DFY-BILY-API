package one.dfy.bily.api.user.model.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.user.constant.UserSearchDateType;
import one.dfy.bily.api.user.dto.UserInfo;
import one.dfy.bily.api.user.model.QUser;
import one.dfy.bily.api.user.model.QLoginHistory;

import one.dfy.bily.api.user.model.repository.UserCustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<UserInfo> findUsersWithRecentLogin(
            String email, UserSearchDateType userSearchDateType, LocalDate date, Boolean isUsed, Pageable pageable
    ) {
        QUser user = QUser.user;
        QLoginHistory loginHistory = QLoginHistory.loginHistory;

        JPQLQuery<UserInfo> baseQuery = queryFactory
                .select(Projections.constructor(
                        UserInfo.class,
                        user.id,
                        user.createdAt,
                        loginHistory.createdAt.max(),
                        user.email,
                        user.name,
                        user.phoneNumber
                ))
                .from(user)
                .leftJoin(loginHistory).on(user.id.eq(loginHistory.userId))
                .where(
                        email != null ? user.email.eq(email) : null,
                        userSearchDateType == UserSearchDateType.SIGNUP ? user.createdAt.goe(date.atStartOfDay()) : null,
                        isUsed != null ? user.used.eq(isUsed) : null
                )
                .groupBy(user.id)
                .having(
                        userSearchDateType == UserSearchDateType.RECENTLY_LOGIN
                                ? loginHistory.createdAt.max().goe(date.atStartOfDay())
                                : null
                )
                .orderBy(user.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<UserInfo> results = baseQuery.fetch();

        long total = queryFactory
                .select(user.countDistinct())
                .from(user)
                .leftJoin(loginHistory).on(user.id.eq(loginHistory.userId))
                .where(
                        email != null ? user.email.eq(email) : null,
                        userSearchDateType == UserSearchDateType.SIGNUP ? user.createdAt.goe(date.atStartOfDay()) : null,
                        isUsed != null ? user.used.eq(isUsed) : null
                )
                .groupBy(user.id)
                .having(
                        userSearchDateType == UserSearchDateType.RECENTLY_LOGIN
                                ? loginHistory.createdAt.max().goe(date.atStartOfDay())
                                : null
                )
                .fetch()
                .size();

        return new PageImpl<>(results, pageable, total);
    }
}




