package one.dfy.bily.api.admin.model.reservation.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.admin.dto.reservation.ReservationResponse;
import one.dfy.bily.api.admin.mapper.ReservationMapper;
import one.dfy.bily.api.admin.model.inquiry.QInquiry;
import one.dfy.bily.api.admin.model.reservation.QReservation;
import one.dfy.bily.api.admin.model.reservation.Reservation;
import one.dfy.bily.api.admin.model.reservation.repository.ReservationCustomRepository;
import one.dfy.bily.api.admin.model.space.QSpace;
import one.dfy.bily.api.common.constant.YesNo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReservationCustomRepositoryImpl implements ReservationCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ReservationResponse> findReservationListByKeywordAndDate(
            String companyName,
            String contactPerson,
            String spaceIdKeyword,
            LocalDateTime startAt,
            LocalDateTime endAt,
            Pageable pageable
    ) {
        QReservation reservation = QReservation.reservation;
        QInquiry inquiry = QInquiry.inquiry;
        QSpace space = QSpace.space;

        // 1. 실제 데이터 조회 (예약 기준)
        List<Reservation> reservations = queryFactory
                .selectFrom(reservation)
                .join(reservation.inquiry, inquiry).fetchJoin()
                .join(inquiry.space, space).fetchJoin()
                .where(
                        companyName != null ? inquiry.companyName.contains(companyName) : null,
                        contactPerson != null ? inquiry.contactPerson.contains(contactPerson) : null,
                        spaceIdKeyword != null ? space.spaceId.contains(spaceIdKeyword) : null,
                        startAt != null ? inquiry.createdAt.goe(startAt) : null,
                        endAt != null ? inquiry.createdAt.loe(endAt) : null,
                        reservation.isUse.eq(YesNo.Y)
                )
                .orderBy(reservation.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 4. Count 쿼리
        Long total = queryFactory
                .select(reservation.count())
                .from(reservation)
                .join(reservation.inquiry, inquiry)
                .join(inquiry.space, space)
                .where(
                        companyName != null ? inquiry.companyName.contains(companyName) : null,
                        contactPerson != null ? inquiry.contactPerson.contains(contactPerson) : null,
                        spaceIdKeyword != null ? space.spaceId.contains(spaceIdKeyword) : null,
                        startAt != null ? inquiry.createdAt.goe(startAt) : null,
                        endAt != null ? inquiry.createdAt.loe(endAt) : null
                )
                .fetchOne();

        List<ReservationResponse> reservationResponseList =
                reservations.stream().map(ReservationMapper::toReservationResponse).toList();

        return new PageImpl<>(reservationResponseList, pageable, total == null ? 0 : total);
    }
}

