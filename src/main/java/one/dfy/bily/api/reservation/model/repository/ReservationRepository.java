package one.dfy.bily.api.reservation.model.repository;

import one.dfy.bily.api.reservation.model.Reservation;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationCustomRepository {

    /**
     * 문의·예약 목록을 한 번에 조회한다.
     * 1) 문의(INQUIRY) – 예약이 존재하면 START/END_DATE 를 가져오고 없으면 NULL
     * 2) 예약(RESERVATION)
     */
    @Query(
            value = """
            (   /* ────── 문의(INQUIRY) ────── */
                SELECT
                    i.ID                      AS id,                 -- 0
                    s.ID                      AS spaceId,            -- 1
                    'INQUIRY'                 AS type,               -- 2
                    s.TITLE                   AS spaceName,          -- 3
                    s.LOCATION                AS location,           -- 4
                    s.AREA_M2                 AS area_m2,            -- 5
                    s.AREA_PY                 AS area_py,            -- 6
                    r.START_DATE              AS reservation_start,  -- 7
                    r.END_DATE                AS reservation_end,    -- 8
                    s.PRICE                   AS price,              -- 9
                    i.STATUS                  AS status,             -- 10
                    i.CREATED_AT              AS created_at,         -- 11
                    r.START_DATE              AS preferred_start_date,-- 12
                    r.END_DATE                AS preferred_end_date,  -- 13
                    'INQUIRY_METADATA'        AS metadata            -- 14
                FROM TBL_INQUIRY i
                JOIN TBL_SPACE s
                  ON i.SPACE_ID = s.ID
                LEFT JOIN TBL_RESERVATION r
                  ON r.INQUIRY_ID = i.ID
                 AND r.USER_ID    = :userId
                 AND r.IS_USED    = 1
                WHERE i.USER_ID = :userId
                  AND i.IS_USED = 1
            )
            UNION ALL
            (   /* ────── 예약(RESERVATION) ────── */
                SELECT
                    r.ID                      AS id,                 -- 0
                    s.ID                      AS spaceId,            -- 1
                    'RESERVATION'             AS type,               -- 2
                    s.TITLE                   AS spaceName,          -- 3
                    s.LOCATION                AS location,           -- 4
                    s.AREA_M2                 AS area_m2,            -- 5
                    s.AREA_PY                 AS area_py,            -- 6
                    r.START_DATE              AS reservation_start,  -- 7
                    r.END_DATE                AS reservation_end,    -- 8
                    s.PRICE                   AS price,              -- 9
                    r.STATUS                  AS status,             -- 10
                    r.CREATED_AT              AS created_at,         -- 11
                    r.START_DATE              AS preferred_start_date,-- 12
                    r.END_DATE                AS preferred_end_date,  -- 13
                    'RESERVATION_METADATA'    AS metadata            -- 14
                FROM TBL_RESERVATION r
                JOIN TBL_INQUIRY i
                  ON r.INQUIRY_ID = i.ID
                JOIN TBL_SPACE   s
                  ON i.SPACE_ID  = s.ID
                WHERE r.USER_ID = :userId
                  AND r.IS_USED = 1
            )
            ORDER BY created_at DESC
            LIMIT :limit OFFSET :offset
            """,
            nativeQuery = true)
    List<Object[]> findReservationAndInquiryRow(@Param("userId") Long userId,
                                                @Param("limit")  int  limit,
                                                @Param("offset") int  offset);



    @Query(value = """
    SELECT COUNT(*) FROM (
        SELECT i.ID FROM TBL_INQUIRY i WHERE i.USER_ID = :userId AND i.IS_USED = true
        UNION ALL
        SELECT r.ID FROM TBL_RESERVATION r WHERE r.USER_ID = :userId AND r.IS_USED = true
    ) AS total
""", nativeQuery = true)
    long countReservationAndInquiry(@Param("userId") Long userId);

}
