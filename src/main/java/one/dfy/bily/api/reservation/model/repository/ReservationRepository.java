package one.dfy.bily.api.reservation.model.repository;

import one.dfy.bily.api.reservation.model.Reservation;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationCustomRepository {

    @Query(value = """
(
  SELECT
    i.ID AS id,                          -- row[0]
    s.ID AS spaceId,                     -- row[1]
    'INQUIRY' AS type,                   -- row[2]
    s.TITLE AS spaceName,                -- row[3]
    s.LOCATION AS location,              -- row[4]
    s.AREA_M2 AS area_m2,                -- row[5]
    s.AREA_PY AS area_py,                -- row[6]
    CAST(NULL AS DATETIME) AS reservation_start, -- row[7], 명시적으로 NULL 처리
    CAST(NULL AS DATETIME) AS reservation_end,   -- row[8], 명시적으로 NULL 처리
    s.PRICE AS price,                    -- row[9]
    i.STATUS AS status,                  -- row[10]
    i.CREATED_AT AS created_at,          -- row[11]
    'INQUIRY_METADATA' AS metadata       -- row[12], 추가 컬럼
  FROM TBL_INQUIRY i
  JOIN TBL_SPACE s ON i.SPACE_ID = s.ID
  WHERE i.USER_ID = :userId AND i.IS_USED = true
)
UNION ALL
(
  SELECT
    r.ID AS id,                          -- row[0]
    s.ID AS spaceId,                     -- row[1]
    'RESERVATION' AS type,               -- row[2]
    s.TITLE AS spaceName,                -- row[3]
    s.LOCATION AS location,              -- row[4]
    s.AREA_M2 AS area_m2,                -- row[5]
    s.AREA_PY AS area_py,                -- row[6]
    r.START_DATE AS reservation_start,   -- row[7]
    r.END_DATE AS reservation_end,       -- row[8]
    s.PRICE AS price,                    -- row[9]
    r.STATUS AS status,                  -- row[10]
    r.CREATED_AT AS created_at,          -- row[11]
    'RESERVATION_METADATA' AS metadata   -- row[12], 추가 컬럼
  FROM TBL_RESERVATION r
  JOIN TBL_INQUIRY i ON r.INQUIRY_ID = i.ID
  JOIN TBL_SPACE s ON i.SPACE_ID = s.ID
  WHERE r.USER_ID = :userId AND r.IS_USED = true
)
ORDER BY created_at DESC
LIMIT :limit OFFSET :offset
""", nativeQuery = true)
    List<Object[]> findReservationAndInquiryRow(@Param("userId") Long userId,
                                                @Param("limit") int limit,
                                                @Param("offset") int offset);


    @Query(value = """
    SELECT COUNT(*) FROM (
        SELECT i.ID FROM TBL_INQUIRY i WHERE i.USER_ID = :userId AND i.IS_USED = true
        UNION ALL
        SELECT r.ID FROM TBL_RESERVATION r WHERE r.USER_ID = :userId AND r.IS_USED = true
    ) AS total
""", nativeQuery = true)
    long countReservationAndInquiry(@Param("userId") Long userId);

}
