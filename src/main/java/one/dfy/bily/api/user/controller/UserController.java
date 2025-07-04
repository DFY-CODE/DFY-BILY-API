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
import one.dfy.bily.api.user.dto.*;
import one.dfy.bily.api.user.model.FindIdRequest;
import one.dfy.bily.api.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/user")
@Tag(name = "회원 API", description = "회원 관련 API")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
//    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "회원 프로필 조회", description = "회원 프로필정보를 반환합니다.")
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
    public ResponseEntity<Profile> profile(
//            @AuthenticationPrincipal CustomUserDetails userDetails
            @RequestParam("") Long userId
    ) {
//        Long userId = userDetails.getUserId();
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
            @Parameter(description = "검색 시작일", required = false) @RequestParam(required = false) LocalDateTime startAt,
            @Parameter(description = "검색 종료일", required = false) @RequestParam(required = false) LocalDateTime endAt,
            @Parameter(description = "예약 검색 페이지", required = false) @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "예약 검색 페이지 사이즈", required = false)
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize
    ) {
        return ResponseEntity.ok(userService.findUserInfoList(email, userSearchDateType, startAt, endAt, page, pageSize));
    }

    @GetMapping("/{id}")
    @Operation(summary = "사용자 상세 조회", description = "사용자 정보 리스트를 반환합니다.")
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
    public ResponseEntity<UserDetailInfo> findUserInfoList(
            @Parameter(description = "회원 아이디", required = false) @PathVariable(name = "id") Long userId
    ) {
        return ResponseEntity.ok(userService.findUserDetailInfo(userId));
    }

    @PatchMapping("")
    @Operation(summary = "회원 탈퇴", description = "회원을 탈퇴 합니다.")
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
    public ResponseEntity<UserCommonResponse> deleteUser(
            @Parameter(description = "이메일", required = false) @RequestParam(required = false) Long userId
    ) {
        return ResponseEntity.ok(userService.deleteUserById(userId));
    }

    @PatchMapping("/password")
    @Operation(summary = "회원 비밀번호 수정", description = "회원 비밀번호를 수정합니다.")
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
    public ResponseEntity<UserCommonResponse> updateUserPassword(
//            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "비밀번호", required = false) @RequestBody() UpdatePassword password
    ) {
//        Long userId = userDetails.getUserId();

        Long userId = 110L;
        return ResponseEntity.ok(userService.updateUserPassword(userId, password));
    }

    @PatchMapping("/phone-number")
    @Operation(summary = "회원 휴대폰 번호 수정", description = "회원 휴대폰 번호를 수정합니다.")
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
    public ResponseEntity<UserCommonResponse> updatePhoneNumber(
//            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "휴대폰 번호", required = false) @RequestBody(required = false) PhoneNumber phoneNumber
    ) {
//        Long userId = userDetails.getUserId();

        Long userId = 110L;
        return ResponseEntity.ok(userService.updatePhoneNumber(userId, phoneNumber));
    }

    @PostMapping("/id")
    @Operation(summary = "아이디 찾기", description = "마스킹된 회원 이메일 정보를 반환합니다.")
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
    public ResponseEntity<MaskingUserEmail> updatePhoneNumber(
           @RequestBody(required = false) FindIdRequest findIdRequest
    ) {
        return ResponseEntity.ok(userService.findMaskingUserEmail(findIdRequest.name(), findIdRequest.phoneNumber()));
    }
}
