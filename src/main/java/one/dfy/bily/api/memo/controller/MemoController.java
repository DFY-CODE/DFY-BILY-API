package one.dfy.bily.api.memo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.dfy.bily.api.inquiry.dto.InquiryResponse;
import one.dfy.bily.api.memo.dto.CreateMemoInfo;
import one.dfy.bily.api.memo.dto.MemoCommonResponse;
import one.dfy.bily.api.memo.dto.MemoResponse;
import one.dfy.bily.api.memo.facade.MemoFacade;
import one.dfy.bily.api.memo.service.MemoService;
import one.dfy.bily.api.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/memo")
@Tag(name = "메모 관리 API", description = "메모 관리 관련 API")
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;
    private final MemoFacade memoFacade;

    @PostMapping()
    @Operation(summary = "메모 생성", description = "메모 생성 후 데이터를 반환합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CreateMemoInfo.class),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            externalValue = "/swagger/json/inquiry/findInquiryByInquiryId.json"
                    )
            )
    )
    public ResponseEntity<MemoCommonResponse> createMemo(@RequestBody CreateMemoInfo createMemoInfo,
                                                         @AuthenticationPrincipal CustomUserDetails userDetails,
                                                         HttpServletRequest request

    ) {
        Long userId;
        if (userDetails != null) {
            userId = userDetails.getUserId();
        } else {
            // local 환경 + Origin 이 localhost:3001 일 때만 세팅된 값
            userId = (Long) request.getAttribute("FALLBACK_USER_ID");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }

        return ResponseEntity.ok(memoFacade.createMemo(createMemoInfo, userId));
    }

    @GetMapping()
    @Operation(summary = "메모 리스트 조회", description = "메모 리스트를 반환합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = MemoResponse.class),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            externalValue = "/swagger/json/inquiry/findInquiryListByKeywordAndDate.json"
                    )
            )
    )
    public ResponseEntity<MemoResponse> findMemoByUserIdAndInquiryId(@RequestParam Long inquiryId,
                                                                     @AuthenticationPrincipal CustomUserDetails userDetails,
                                                                     HttpServletRequest request

    ){
        Long userId;
        if (userDetails != null) {
            userId = userDetails.getUserId();
        } else {
            // local 환경 + Origin 이 localhost:3001 일 때만 세팅된 값
            userId = (Long) request.getAttribute("FALLBACK_USER_ID");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }


        // Long userId = 110L;
        return ResponseEntity.ok(memoFacade.findMemoByUserIdAndInquiryId(userId, inquiryId));
    }

    @PatchMapping
    @Operation(summary = "메모 삭제", description = "메모를 삭제합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = InquiryResponse.class)),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            externalValue = "/swagger/json/inquiry/findInquiryListByKeywordAndDate.json"
                    )
            )
    )
    public ResponseEntity<MemoCommonResponse> deleteMemo(@RequestParam Long memoId){
        return ResponseEntity.ok(memoService.deleteMemo(memoId));
    }
}
