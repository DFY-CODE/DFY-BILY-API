package one.dfy.bily.api.admin.model.inquiry.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.admin.dto.Inquiry.InquiryFileName;
import one.dfy.bily.api.admin.dto.Inquiry.InquiryResponse;
import one.dfy.bily.api.admin.mapper.InquiryMapper;
import one.dfy.bily.api.admin.model.inquiry.*;
import one.dfy.bily.api.admin.model.inquiry.repository.InquiryRepositoryCustom;
import one.dfy.bily.api.admin.model.space.QSpace;
import one.dfy.bily.api.common.constant.YesNo;
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
            String spaceIdKeyword,
            LocalDateTime startAt,
            LocalDateTime endAt,
            Pageable pageable
    ) {
        QInquiry inquiry = QInquiry.inquiry;
        QInquiryFile file = QInquiryFile.inquiryFile;
        QSpace space = QSpace.space;
        QPreferredDate inquiryPreferredDate = QPreferredDate.preferredDate;

        // 1. 조회
        List<Inquiry> inquiries = queryFactory
                .selectFrom(inquiry)
                .leftJoin(inquiry.space, space).fetchJoin()
                .where(
                        companyName != null ? inquiry.companyName.contains(companyName) : null,
                        contactPerson != null ? inquiry.contactPerson.contains(contactPerson) : null,
                        spaceIdKeyword != null ? space.spaceId.contains(spaceIdKeyword) : null,
                        startAt != null ? inquiry.createdAt.goe(startAt) : null,
                        endAt != null ? inquiry.createdAt.loe(endAt) : null,
                        inquiry.isUse.eq(YesNo.Y)
                )
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
                        spaceIdKeyword != null ? space.spaceId.containsIgnoreCase(spaceIdKeyword) : null,
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
}