package one.dfy.bily.api.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.dfy.bily.api.inquiry.constant.InquirySearchType;
import one.dfy.bily.api.reservation.constant.ReservationStatus;
import one.dfy.bily.api.reservation.dto.*;
import one.dfy.bily.api.reservation.facade.ReservationFacade;
import one.dfy.bily.api.reservation.service.ReservationService;
import one.dfy.bily.api.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/reservation")
@Tag(name = "예약 관리 API", description = "예약 관리 관련 API")
@RequiredArgsConstructor
public class ReservationApiController {
    private final ReservationService reservationService;
    private final ReservationFacade reservationFacade;

    @GetMapping()
    @Operation(summary = "예약 리스트 조회", description = "검색 타입과 키워드를 통해 예약 리스트를 반환합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ReservationResponse.class)),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            externalValue = "/swagger/json/reservation/findInquiryListByKeywordAndDate.json"
                    )
            )
    )
    public ResponseEntity<ReservationListResponse> findInquiryListByKeywordAndDate(
            @Parameter(
                    description = "문의 검색 타입 (공간명, 회사명, 이름)",
                    required = false,
                    schema = @Schema(
                            type = "string",
                            allowableValues = {"공간명", "회사명", "이름"}
                    )
            )
            @RequestParam(value = "type", required = false) InquirySearchType type,
            @Parameter(description = "예약 검색 단어", required = false) @RequestParam(value = "keyword", required = false) String keyword,
            @Parameter(
                    description = "예약 검색 시작일 (예: 2025-04-06T00:00:00)",
                    required = false,
                    schema = @Schema(type = "string", format = "date-time", example = "2025-04-06T00:00:00")
            )
            @RequestParam(value = "start_date", required = false) LocalDateTime startAt,
            @Parameter(
                    description = "예약 검색 종료일 (예: 2025-04-06T23:59:59)",
                    required = false,
                    schema = @Schema(type = "string", format = "date-time", example = "2025-04-06T23:59:59")
            )
            @RequestParam(value = "end_date", required = false) LocalDateTime endAt,
            @Parameter(description = "예약 검색 페이지", required = false) @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "예약 검색 페이지 사이즈", required = false)
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize,
            @RequestParam(value = "status", required = false) List<ReservationStatus> reservationStatusList
    ) {
        return ResponseEntity.ok(reservationService.findReservationListByKeywordAndDate(type, keyword, startAt, endAt, page, pageSize, reservationStatusList));
    }

    @GetMapping("/{reservation-id}")
    @Operation(summary = "예약 상세 조회", description = "예약 상세정보를 반환합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ReservationDetailResponse.class),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            externalValue = "/swagger/json/reservation/findReservationDetail.json"
                    )
            )
    )
    public ResponseEntity<ReservationDetailResponse> findReservationDetail(@PathVariable(name = "reservation-id") Long reservationId) {
        return ResponseEntity.ok(reservationFacade.findReservationDetail(reservationId));
    }

    @GetMapping("/payment/{reservation-id}")
    @Operation(summary = "예약 상태 및 견적 조회", description = "예약 상태 및 견적 정보를 반환합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ReservationPaymentInfo.class),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            externalValue = "/swagger/json/reservation/findReservationPaymentById.json"
                    )
            )
    )
    public ResponseEntity<ReservationPaymentInfo> findReservationPaymentById(@PathVariable(name = "reservation-id") Long reservationId) {
        return ResponseEntity.ok(reservationService.findReservationPaymentById(reservationId));
    }

    @PostMapping
    @Operation(summary = "예약 생성", description = "예약 생성 후 생성내용을 반환합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ReservationPaymentInfo.class),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            externalValue = "/swagger/json/reservation/updateReservation.json"
                    )
            )
    )
    public ResponseEntity<ReservationPaymentInfo> createReservationPayment(
            @RequestBody CreateReservation createReservation
//            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
//        Long adminId = userDetails.getUserId();
        Long adminId = 110L;
        return ResponseEntity.ok(reservationFacade.createReservationPayment(createReservation, adminId));
    }

    @PatchMapping("/{reservation-id}")
    @Operation(summary = "예약 수정", description = "예약 수정 후 수정내용을 반환합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ReservationPaymentInfo.class),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            externalValue = "/swagger/json/reservation/updateReservation.json"
                    )
            )
    )
    public ResponseEntity<ReservationPaymentInfo> updateReservation(
            @PathVariable(name = "reservation-id") Long reservationId,
            @RequestBody ReservationPaymentInfo reservationPaymentInfo
//            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
//        Long adminId = userDetails.getUserId();
        Long adminId = 110L;
        return ResponseEntity.ok(reservationService.updateReservation(reservationId,reservationPaymentInfo, adminId));
    }
}
