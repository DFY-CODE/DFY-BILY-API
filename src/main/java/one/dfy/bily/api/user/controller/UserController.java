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
import one.dfy.bily.api.user.constant.UserSearchDateType;
import one.dfy.bily.api.user.dto.UserActivityList;
import one.dfy.bily.api.user.dto.Profile;
import one.dfy.bily.api.user.dto.UserInfoList;
import one.dfy.bily.api.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/user")
@Tag(name = "회원 API", description = "회원 관련 API")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "사용자 프로필 조회", description = "사용자 프로필정보를 반환합니다.")
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
    public ResponseEntity<Profile> profile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        return ResponseEntity.ok(userService.findProfileById(userId));
    }

    @GetMapping("")
    @Operation(summary = "사용자 리스트 조회", description = "사용자 정보 리스트를 반환합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserInfoList.class),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            externalValue = "/swagger/json/user/activity/findReservation.json"
                    )
            )
    )
    public ResponseEntity<UserInfoList> findUserInfoList(
            @Parameter(description = "이메일", required = false) @RequestParam(required = false) String email,
            @Parameter(description = "기간 타입", required = false) @RequestParam(required = false) UserSearchDateType userSearchDateType,
            @Parameter(description = "기간", required = false) @RequestParam(required = false) LocalDate recentLoginDate,
            @Parameter(description = "예약 검색 페이지", required = false) @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "예약 검색 페이지 사이즈", required = false)
            @RequestParam(value = "page_size", defaultValue = "20") int pageSize
    ) {
        return ResponseEntity.ok(userService.findUserInfoList(email, userSearchDateType, recentLoginDate, page, pageSize));
    }
}
