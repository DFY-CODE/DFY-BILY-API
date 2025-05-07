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
        i.ID AS id,
        i.ID AS content_id,
        'INQUIRY' AS type,
        s.NAME AS space_name,
        s.LOCATION AS location,
        s.AREA_M2 AS area_m2,
        s.AREA_PY AS area_py,
        s.MAX_CAPACITY AS max_capacity,
        NULL AS reservation_start,
        NULL AS reservation_end,
        s.PRICE AS price,
        i.STATUS AS status,
        i.CREATED_AT AS created_at
      FROM TBL_INQUIRY i
      JOIN TBL_SPACE s ON i.CONTENT_ID = s.CONTENT_ID
      WHERE i.USER_ID = :userId AND i.IS_USE = 'Y'
    )
    UNION ALL
    (
      SELECT
        r.ID AS id,
        i.ID AS content_id,
        'RESERVATION' AS type,
        s.NAME AS space_name,
        s.LOCATION AS location,
        s.AREA_M2 AS area_m2,
        s.AREA_PY AS area_py,
        s.MAX_CAPACITY AS max_capacity,
        r.START_DATE AS reservation_start,
        r.END_DATE AS reservation_end,
        s.PRICE AS price,
        r.STATUS AS status,
        r.CREATED_AT AS created_at
      FROM TBL_RESERVATION r
      JOIN TBL_INQUIRY i ON r.INQUIRY_ID = i.ID
      JOIN TBL_SPACE s ON i.CONTENT_ID = s.CONTENT_ID
      WHERE r.USER_ID = :userId AND r.IS_USE = 'Y'
    )
     ORDER BY created_at DESC
    LIMIT :limit OFFSET :offset
""", nativeQuery = true)
    List<Object[]> findReservationAndInquiryRow(@Param("userId") Long userId,
                                                @Param("limit") int limit,
                                                @Param("offset") int offset);

    @Query(value = """
    SELECT COUNT(*) FROM (
        SELECT i.ID FROM TBL_INQUIRY i WHERE i.USER_ID = :userId AND i.IS_USE = 'Y'
        UNION ALL
        SELECT r.ID FROM TBL_RESERVATION r WHERE r.USER_ID = :userId AND r.IS_USE = 'Y'
    ) AS total
""", nativeQuery = true)
    long countReservationAndInquiry(@Param("userId") Long userId);

}
