package one.dfy.bily.api.reservation.model.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.inquiry.model.QInquiry;
import one.dfy.bily.api.reservation.constant.ReservationStatus;
import one.dfy.bily.api.reservation.dto.ReservationResponse;
import one.dfy.bily.api.reservation.mapper.ReservationMapper;
import one.dfy.bily.api.reservation.model.QReservation;
import one.dfy.bily.api.reservation.model.Reservation;
import one.dfy.bily.api.reservation.model.repository.ReservationCustomRepository;
import one.dfy.bily.api.common.constant.YesNo;
import one.dfy.bily.api.space.model.QSpace;
import one.dfy.bily.api.space.model.QSpaceFileInfo;
import one.dfy.bily.api.user.dto.ReservationActivity;
import one.dfy.bily.api.user.dto.UserActivity;
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
    private final EntityManager em;

    @Override
    public Page<ReservationResponse> findReservationListByKeywordAndDate(
            String companyName,
            String contactPerson,
            String alias,
            LocalDateTime startAt,
            LocalDateTime endAt,
            Pageable pageable,
            List<ReservationStatus> reservationStatusList
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
                        alias != null ? space.alias.contains(alias) : null,
                        startAt != null ? inquiry.createdAt.goe(startAt) : null,
                        endAt != null ? inquiry.createdAt.loe(endAt) : null,
                        reservation.used.eq(true),
                        (reservationStatusList == null || reservationStatusList.isEmpty())
                                ? Expressions.FALSE
                                : reservation.status.in(reservationStatusList)
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
                        alias != null ? space.alias.contains(alias) : null,
                        startAt != null ? inquiry.createdAt.goe(startAt) : null,
                        endAt != null ? inquiry.createdAt.loe(endAt) : null,
                        (reservationStatusList == null || reservationStatusList.isEmpty())
                                ? Expressions.FALSE
                                : reservation.status.in(reservationStatusList)
                )
                .fetchOne();

        List<ReservationResponse> reservationResponseList =
                reservations.stream().map(ReservationMapper::toReservationResponse).toList();

        return new PageImpl<>(reservationResponseList, pageable, total == null ? 0 : total);
    }

    @Override
    public Page<ReservationActivity> findReservationListByUserId(Long userId, Pageable pageable) {
        QReservation reservation = QReservation.reservation;
        QInquiry inquiry = QInquiry.inquiry;
        QSpace space = QSpace.space;

        // 데이터 조회 (페이징 적용)
        List<ReservationActivity> contents = queryFactory
                .select(Projections.constructor(ReservationActivity.class,
                        reservation.id,
                        space.id,
                        space.title,
                        space.location,
                        space.areaM2,
                        space.areaPy,
                        reservation.startDate,
                        reservation.endDate,
                        space.price,
                        reservation.status,
                        reservation.createdAt
                ))
                .from(reservation)
                .join(reservation.inquiry, inquiry)
                .join(inquiry.space, space)
                .where(reservation.userId.eq(userId), reservation.used.eq(true))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수 조회
        Long total = queryFactory
                .select(reservation.count())
                .from(reservation)
                .join(reservation.inquiry, inquiry)
                .join(inquiry.space, space)
                .where(reservation.userId.eq(userId), reservation.used.eq(true))
                .fetchOne();

        return new PageImpl<>(contents, pageable, total != null ? total : 0L);
    }

}

