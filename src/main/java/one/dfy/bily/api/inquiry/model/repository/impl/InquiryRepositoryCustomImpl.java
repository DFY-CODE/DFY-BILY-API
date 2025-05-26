package one.dfy.bily.api.inquiry.model.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.inquiry.constant.InquiryStatus;
import one.dfy.bily.api.inquiry.dto.InquiryResponse;
import one.dfy.bily.api.inquiry.mapper.InquiryMapper;
import one.dfy.bily.api.inquiry.model.*;
import one.dfy.bily.api.inquiry.model.repository.InquiryRepositoryCustom;
import one.dfy.bily.api.common.constant.YesNo;
import one.dfy.bily.api.space.model.QSpace;
import one.dfy.bily.api.user.dto.InquiryActivity;
import one.dfy.bily.api.user.dto.UserActivity;
import one.dfy.bily.api.util.AES256Util;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InquiryRepositoryCustomImpl implements InquiryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<InquiryResponse> searchInquiries(
            String companyName,
            String contactPerson,
            String alias,
            LocalDateTime startAt,
            LocalDateTime endAt,
            Pageable pageable,
            List<InquiryStatus> statusList
    ) {
        QInquiry inquiry = QInquiry.inquiry;
        QInquiryFile file = QInquiryFile.inquiryFile;
        QSpace space = QSpace.space;
        QPreferredDate inquiryPreferredDate = QPreferredDate.preferredDate;

        BooleanBuilder condition = buildInquirySearchCondition(
                inquiry, space, companyName, contactPerson, alias, startAt, endAt, statusList
        );

        // 1. 조회
        List<Inquiry> inquiries = queryFactory
                .selectFrom(inquiry)
                .leftJoin(inquiry.space, space).fetchJoin()
                .where(condition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(inquiry.id.desc())
                .fetch();

        // 2. ID 리스트 뽑기
        List<Long> inquiryIds = inquiries.stream().map(Inquiry::getId).toList();

        // 3. 파일 목록 조회 및 그룹핑
        Map<Long, List<InquiryFile>> filesById = queryFactory
                .select(file, file.inquiry.id)
                .from(file)
                .where(file.inquiry.id.in(inquiryIds))
                .fetch()
                .stream()
                .filter(tuple -> tuple.get(1, Long.class) != null)
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(1, Long.class), // inquiry.id
                        Collectors.mapping(
                                tuple -> tuple.get(0, InquiryFile.class), // InquiryFileInfo 엔티티
                                Collectors.toList()
                        )
                ));

        // 4. 희망 날짜 조회 및 그룹핑
        Map<Long, List<PreferredDate>> preferredDatesByInquiryId = queryFactory
                .selectFrom(inquiryPreferredDate)
                .where(inquiryPreferredDate.inquiry.id.in(inquiryIds))
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        pd -> pd.getInquiry().getId()
                ));

        // 4. 총 개수 조회
        Long total = queryFactory
                .select(inquiry.count())
                .from(inquiry)
                .leftJoin(inquiry.space, space)
                .where(
                        companyName != null ? inquiry.companyName.eq(companyName) : null,
                        contactPerson != null ? inquiry.contactPerson.eq(contactPerson) : null,
                        alias != null ? space.alias.containsIgnoreCase(alias) : null,
                        startAt != null ? inquiry.createdAt.goe(startAt) : null,
                        endAt != null ? inquiry.createdAt.loe(endAt) : null
                )
                .fetchOne();

        // 5. DTO 매핑
        List<InquiryResponse> content = inquiries.stream()
                .map(i -> InquiryMapper.toInquiryResponse(i, filesById, preferredDatesByInquiryId))
                .toList();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    @Override
    public Page<InquiryActivity> findInquiryActivitiesByUserId(Long userId, Pageable pageable) {
        QInquiry inquiry = QInquiry.inquiry;
        QSpace space = QSpace.space;

        // fetch 결과 후 암호화 처리
        List<InquiryActivity> contents = queryFactory
                .select(Projections.constructor(InquiryActivity.class,
                        inquiry.id,
                        space.title,
                        space.location,
                        space.areaM2,
                        space.areaPy,
                        space.price,
                        inquiry.status,
                        inquiry.createdAt
                ))
                .from(inquiry)
                .join(inquiry.space, space)
                .where(inquiry.userId.eq(userId), inquiry.used.eq(true))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(activity -> new InquiryActivity(
                        activity.id(),
                        activity.spaceName(),
                        activity.location(),
                        activity.areaM2(),
                        activity.areaPy(),
                        activity.price(),
                        activity.status(),
                        activity.createdAt()
                ))
                .collect(Collectors.toList());

        Long total = queryFactory
                .select(inquiry.count())
                .from(inquiry)
                .join(inquiry.space, space)
                .where(inquiry.userId.eq(userId), inquiry.used.eq(true))
                .fetchOne();

        return new PageImpl<>(contents, pageable, total != null ? total : 0L);
    }

    private String encryptSpaceId(Long id) {
        try {
            return AES256Util.encrypt(id);
        } catch (Exception e) {
            throw new RuntimeException("SpaceId 암호화 실패", e);
        }
    }


    private BooleanBuilder buildInquirySearchCondition(
            QInquiry inquiry,
            QSpace space,
            String companyName,
            String contactPerson,
            String alias,
            LocalDateTime startAt,
            LocalDateTime endAt,
            List<InquiryStatus> statusList
    ) {
        BooleanBuilder condition = new BooleanBuilder();

        if (companyName != null) condition.and(inquiry.companyName.contains(companyName));
        if (contactPerson != null) condition.and(inquiry.contactPerson.contains(contactPerson));
        if (alias != null) condition.and(space.alias.contains(alias));
        if (startAt != null) condition.and(inquiry.createdAt.goe(startAt));
        if (endAt != null) condition.and(inquiry.createdAt.loe(endAt));
        condition.and(inquiry.used.eq(true));

        if (statusList != null && !statusList.isEmpty()) {
            condition.and(inquiry.status.in(statusList));
        } else {
            condition.and(inquiry.id.eq(-1L)); // 조회 차단용 조건
        }

        return condition;
    }

}