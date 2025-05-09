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
import one.dfy.bily.api.security.CustomUserDetails;
import one.dfy.bily.api.user.dto.SavedSpaceList;
import one.dfy.bily.api.user.dto.UserActivityList;
import one.dfy.bily.api.user.facade.UserActivityFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
                    schema = @Schema(implementation = UserActivityList.class),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            externalValue = "/swagger/json/user/activity/findReservationAndInquiry.json"
                    )
            )
    )
    public ResponseEntity<UserActivityList> findReservationAndInquiry(
            @Parameter(description = "예약 검색 페이지", required = false) @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "예약 검색 페이지 사이즈", required = false)
            @RequestParam(value = "page_size", defaultValue = "20") int pageSize,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        return ResponseEntity.ok(userActivityFacade.findReservationAndInquiryListByUserId(userId,page,pageSize));
    }

    @GetMapping("/inquiry")
    @Operation(summary = "나의 상담 리스트 조회", description = "나의 상담 리스트정보를 반환합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserActivityList.class),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            externalValue = "/swagger/json/user/activity/findInquiry.json"
                    )
            )
    )
    public ResponseEntity<UserActivityList> findInquiry(
            @Parameter(description = "예약 검색 페이지", required = false) @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "예약 검색 페이지 사이즈", required = false)
            @RequestParam(value = "page_size", defaultValue = "20") int pageSize,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        return ResponseEntity.ok(userActivityFacade.findInquiryListByUserId(userId,page,pageSize));
    }

    @GetMapping("/reservation")
    @Operation(summary = "나의 예약 리스트 조회", description = "나의 예약 리스트정보를 반환합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserActivityList.class),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            externalValue = "/swagger/json/user/activity/findReservation.json"
                    )
            )
    )
    public ResponseEntity<UserActivityList> findReservation(
            @Parameter(description = "예약 검색 페이지", required = false) @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "예약 검색 페이지 사이즈", required = false)
            @RequestParam(value = "page_size", defaultValue = "20") int pageSize,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        return ResponseEntity.ok(userActivityFacade.findReservationListByUserId(userId,page,pageSize));
    }

    @GetMapping("/saved-space")
    @Operation(summary = "나의 공간 저장 리스트 조회", description = "나의 공간 저장 리스트정보를 반환합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserActivityList.class),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            externalValue = "/swagger/json/user/activity/findSavedSpace.json"
                    )
            )
    )
    public ResponseEntity<SavedSpaceList> findSavedSpaceByUserId(
            @Parameter(description = "유저 번호", required = false) @RequestParam(defaultValue = "1") Long userId,
            @Parameter(description = "예약 검색 페이지", required = false) @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "예약 검색 페이지 사이즈", required = false)
            @RequestParam(value = "page_size", defaultValue = "20") int pageSize
//            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
//        Long userId = userDetails.getUserId();
        return ResponseEntity.ok(userActivityFacade.findSavedSpaceByUserId(userId,page,pageSize));
    }
}
