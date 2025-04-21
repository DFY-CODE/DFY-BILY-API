package one.dfy.bily.api.admin.model.rent.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.admin.dto.InquiryResponse;
import one.dfy.bily.api.admin.dto.InquiryPreferredDate;
import one.dfy.bily.api.admin.dto.InquerySpaces;
import one.dfy.bily.api.admin.mapper.InquiryMapper;
import one.dfy.bily.api.admin.model.rent.Inquiry;
import one.dfy.bily.api.admin.model.rent.QInquiry;
import one.dfy.bily.api.admin.model.rent.QInquiryFileInfo;
import one.dfy.bily.api.admin.model.rent.repository.InquiryRepositoryCustom;
import one.dfy.bily.api.admin.model.space.QSpace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

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
            Pageable pageable
    ) {
        QInquiry inquiry = QInquiry.inquiry;
        QInquiryFileInfo file = QInquiryFileInfo.inquiryFileInfo;
        QSpace space = QSpace.space;

        // 1. 조회
        List<Inquiry> inquiries = queryFactory
                .selectFrom(inquiry)
                .leftJoin(inquiry.space, space).fetchJoin()
                .where(
                        companyName != null ? inquiry.companyName.contains(companyName) : null,
                        contactPerson != null ? inquiry.contactPerson.contains(contactPerson) : null,
                        spaceIdKeyword != null ? space.spaceId.contains(spaceIdKeyword) : null
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(inquiry.inquiryId.desc())
                .fetch();

        // 2. ID 리스트 뽑기
        List<Long> inquiryIds = inquiries.stream().map(Inquiry::getInquiryId).toList();

        // 3. 파일 목록 조회 및 그룹핑
        Map<Long, List<String>> filesByInquiryId = queryFactory
                .select(file.inquiry.inquiryId, file.fileName)
                .from(file)
                .where(file.inquiry.inquiryId.in(inquiryIds))
                .fetch()
                .stream()
                .filter(tuple -> tuple.get(file.inquiry.inquiryId) != null)
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(file.inquiry.inquiryId),
                        Collectors.mapping(tuple -> tuple.get(file.fileName), Collectors.toList())
                ));

        // 4. 총 개수 조회
        Long total = queryFactory
                .select(inquiry.count())
                .from(inquiry)
                .leftJoin(inquiry.space, space)
                .where(
                        companyName != null ? inquiry.companyName.eq(companyName) : null,
                        contactPerson != null ? inquiry.contactPerson.eq(contactPerson) : null,
                        spaceIdKeyword != null ? space.spaceId.containsIgnoreCase(spaceIdKeyword) : null
                )
                .fetchOne();

        // 5. DTO 매핑
        List<InquiryResponse> content = inquiries.stream()
                .map(i -> InquiryMapper.toInquiryResponse(i, filesByInquiryId))
                .toList();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }
}