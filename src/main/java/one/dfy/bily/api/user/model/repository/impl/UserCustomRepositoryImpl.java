package one.dfy.bily.api.user.model.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.user.constant.UserSearchDateType;
import one.dfy.bily.api.user.constant.UserStatus;
import one.dfy.bily.api.user.dto.UserInfo;
import one.dfy.bily.api.user.model.QSignInHistory;
import one.dfy.bily.api.user.model.QUser;
import one.dfy.bily.api.user.model.repository.UserCustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<UserInfo> findUsersWithRecentLogin(
            String email, UserSearchDateType userSearchDateType, LocalDateTime startAt, LocalDateTime endAt, Boolean isUsed, Pageable pageable
    ) {
        QUser user = QUser.user;
        QSignInHistory signInHistory = QSignInHistory.signInHistory;

        BooleanBuilder whereBuilder = new BooleanBuilder();
        if (email != null) whereBuilder.and(user.email.eq(email));
        if (isUsed != null && isUsed) whereBuilder.and(user.status.eq(UserStatus.ACTIVE));

        if (userSearchDateType == UserSearchDateType.SIGNUP) {
            if (startAt != null) whereBuilder.and(user.createdAt.goe(startAt));
            if (endAt != null) whereBuilder.and(user.createdAt.loe(endAt));
        }

        BooleanBuilder havingBuilder = new BooleanBuilder();
        if (userSearchDateType == UserSearchDateType.RECENTLY_LOGIN) {
            if (startAt != null) havingBuilder.and(signInHistory.createdAt.max().goe(startAt));
            if (endAt != null) havingBuilder.and(signInHistory.createdAt.max().loe(endAt));
        }

        JPQLQuery<UserInfo> baseQuery = queryFactory
                .select(Projections.constructor(
                        UserInfo.class,
                        user.id,
                        user.createdAt,
                        signInHistory.createdAt.max(),
                        user.email,
                        user.name,
                        user.phoneNumber
                ))
                .from(user)
                .leftJoin(signInHistory).on(user.id.eq(signInHistory.userId))
                .where(whereBuilder)
                .groupBy(user.id)
                .having(havingBuilder)
                .orderBy(user.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<UserInfo> results = baseQuery.fetch();

        long total = queryFactory
                .select(user.countDistinct())
                .from(user)
                .leftJoin(signInHistory).on(user.id.eq(signInHistory.userId))
                .where(whereBuilder)
                .groupBy(user.id)
                .having(havingBuilder)
                .fetch().size();

        return new PageImpl<>(results, pageable, total);
    }
}
