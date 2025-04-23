package one.dfy.bily.api.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.dfy.bily.api.admin.constant.InquirySearchType;
import one.dfy.bily.api.admin.dto.Inquiry.InquiryResponse;
import one.dfy.bily.api.admin.dto.reservation.ReservationDetailResponse;
import one.dfy.bily.api.admin.dto.reservation.ReservationResponse;
import one.dfy.bily.api.admin.dto.reservation.ReservationUpdateRequest;
import one.dfy.bily.api.admin.facade.ReservationFacade;
import one.dfy.bily.api.admin.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@Tag(name = "예약 관리 API", description = "예약 관리 관련 API")
@RequiredArgsConstructor
public class ReservationApiController {
    private ReservationService reservationService;
    private ReservationFacade reservationFacade;

    @GetMapping()
    @Operation(summary = "예약 리스트 조회", description = "검색 타입과 키워드를 통해 예약 리스트를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            externalValue = "/swagger/json/inquiry/findInquiryListByKeywordAndDate.json"
                    )
            ))
    })
    public ResponseEntity<List<ReservationResponse>> findInquiryListByKeywordAndDate(
            @Parameter(description = "예약 검색 타입", required = false) @RequestParam(value = "type", required = false) InquirySearchType type,
            @Parameter(description = "예약 검색 단어", required = false) @RequestParam(value = "keyword", required = false)String keyword,
            @Parameter(
                    description = "예약 검색 시작일 (예: 2025-04-06T00:00:00)",
                    required = false,
                    schema = @Schema(type = "string", format = "date-time", example = "2025-04-06T00:00:00")
            )
            @RequestParam(value = "start-date", required = false) LocalDateTime startAt,
            @Parameter(
                    description = "예약 검색 종료일 (예: 2025-04-06T23:59:59)",
                    required = false,
                    schema = @Schema(type = "string", format = "date-time", example = "2025-04-06T23:59:59")
            )
            @RequestParam(value = "end-date", required = false) LocalDateTime endAt,
            @Parameter(description = "예약 검색 페이지", required = false) @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "예약 검색 페이지 사이즈", required = false) @RequestParam(defaultValue = "20") int pageSize
    ) {
        return ResponseEntity.ok(reservationService.findReservationListByKeywordAndDate(type, keyword, startAt, endAt, page, pageSize));
    }

    @GetMapping("/{reservation-id}")
    @Operation(summary = "예약 상세 조회", description = "예약 상세정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            externalValue = "/swagger/json/inquiry/findInquiryListByKeywordAndDate.json"
                    )
            ))
    })
    public ResponseEntity<ReservationDetailResponse> findReservationDetail(@PathVariable(name = "reservation-id") Long reservationId) {
        return ResponseEntity.ok(reservationFacade.findReservationDetail(reservationId));
    }

    @PatchMapping()
    @Operation(summary = "예약 리스트 조회", description = "검색 타입과 키워드를 통해 예약 리스트를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            externalValue = "/swagger/json/inquiry/findInquiryListByKeywordAndDate.json"
                    )
            ))
    })
    public ResponseEntity<ReservationUpdateRequest> updateReservation(@RequestBody ReservationUpdateRequest reservationUpdateRequest) {
        return ResponseEntity.ok(reservationService.updateReservation(reservationUpdateRequest));
    }

}
