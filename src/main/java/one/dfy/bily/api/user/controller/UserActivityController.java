package one.dfy.bily.api.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.dfy.bily.api.reservation.dto.ReservationAndInquiryListResponse;
import one.dfy.bily.api.reservation.dto.ReservationDetailResponse;
import one.dfy.bily.api.user.facade.UserActivityFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/user/activity")
@Tag(name = "회원 활동 API", description = "회원 활동 관련 API")
@RequiredArgsConstructor
public class UserActivityController {

    private final UserActivityFacade userActivityFacade;

    @GetMapping("/reservation-inquiry")
    @Operation(summary = "나의 예약 및 상담 리스트 조회", description = "나의 예약 및 상담 리스트정보를 반환합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ReservationAndInquiryListResponse.class),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            externalValue = "/swagger/json/user/activity/findReservationAndInquiry.json"
                    )
            )
    )
    public ResponseEntity<ReservationAndInquiryListResponse> findReservationAndInquiry(
            @Parameter(description = "회원 아이디(번호)", required = false) Long userId,
            @Parameter(description = "예약 검색 페이지", required = false) @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "예약 검색 페이지 사이즈", required = false)
            @RequestParam(value = "page_size", defaultValue = "20") int pageSize
    ) {
        return ResponseEntity.ok(userActivityFacade.findReservationAndInquiryListByUserId(userId,page,pageSize));
    }
}
