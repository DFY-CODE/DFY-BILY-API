package one.dfy.bily.api.admin.controller;

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
import one.dfy.bily.api.admin.constant.InquirySearchType;
import one.dfy.bily.api.admin.dto.Inquiry.InquiryCreateRequest;
import one.dfy.bily.api.admin.dto.Inquiry.InquiryResponse;
import one.dfy.bily.api.admin.dto.Inquiry.InquiryStatusUpdateRequest;
import one.dfy.bily.api.admin.dto.Inquiry.InquiryUpdateRequest;
import one.dfy.bily.api.admin.facade.InquiryFacade;
import one.dfy.bily.api.admin.service.InquiryService;
import one.dfy.bily.api.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/inquiry")
@Tag(name = "문의 관리 API", description = "문의 관리 관련 API")
@RequiredArgsConstructor
public class InquiryApiController {

    private final InquiryService inquiryService;
    private final InquiryFacade inquiryFacade;

    @GetMapping()
    @Operation(summary = "문의 리스트 조회", description = "검색 타입과 키워드를 통해 문의 리스트를 반환합니다.")
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
    public ResponseEntity<List<InquiryResponse>> findInquiryListByKeywordAndDate(
            @Parameter(
                    description = "문의 검색 타입 (공간명, 회사명, 이름)",
                    required = false,
                    schema = @Schema(
                            type = "string",
                            allowableValues = {"공간명", "회사명", "이름"}
                    )
            )
            @RequestParam(value = "type", required = false) InquirySearchType type,
            @Parameter(description = "문의 검색 단어", required = false) @RequestParam(value = "keyword", required = false) String keyword,
            @Parameter(
                    description = "문의 검색 시작일 (예: 2025-04-06T00:00:00)",
                    required = false,
                    schema = @Schema(type = "string", format = "date-time", example = "2025-04-06T00:00:00")
            )
            @RequestParam(value = "start_date", required = false) LocalDateTime startAt,
            @Parameter(
                    description = "문의 검색 종료일 (예: 2025-04-06T23:59:59)",
                    required = false,
                    schema = @Schema(type = "string", format = "date-time", example = "2025-04-06T23:59:59")
            )
            @RequestParam(value = "end_date", required = false) LocalDateTime endAt,
            @Parameter(description = "문의 검색 페이지", required = false) @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "문의 검색 페이지 사이즈", required = false)
            @RequestParam(value = "page_size", defaultValue = "20") int pageSize
    ) {
        return ResponseEntity.ok(inquiryService.findInquiryListByKeywordAndDate(type, keyword, startAt, endAt, page, pageSize));
    }

    @GetMapping("/{inquiry-id}")
    @Operation(summary = "문의 상세 조회", description = "문의 아이디로 상세 데이터를 반환합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = InquiryResponse.class),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            externalValue = "/swagger/json/inquiry/findInquiryByInquiryId.json"
                    )
            )
    )
    public ResponseEntity<InquiryResponse> findInquiryByInquiryId(@PathVariable(name = "inquiry-id") Long inquiryId) {
        return ResponseEntity.ok(inquiryService.findInquiryByInquiryId(inquiryId));
    }

    @PostMapping(consumes = "multipart/form-data")
    @Operation(summary = "문의 생성", description = "문의 생성 후 데이터를 반환합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = InquiryCreateRequest.class),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            externalValue = "/swagger/json/inquiry/findInquiryByInquiryId.json"
                    )
            )
    )
    public ResponseEntity<InquiryResponse> createInquiry(@RequestPart InquiryCreateRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        return ResponseEntity.ok(inquiryFacade.createInquiry(request,userId));
    }

    @PatchMapping("/{inquiry-id}")
    @Operation(summary = "문의 수정", description = "문의 내용을 수정합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = InquiryUpdateRequest.class),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            externalValue = "/swagger/json/inquiry/findInquiryByInquiryId.json"
                    )
            )
    )
    public ResponseEntity<InquiryResponse> updateInquiry(
            @PathVariable(name = "inquiry-id") Long inquiryId,
            @RequestBody InquiryUpdateRequest request
    ) {
        return ResponseEntity.ok(inquiryFacade.updateInquiry(inquiryId, request));
    }

    @PatchMapping("/status/{inquiry-id}")
    @Operation(summary = "문의 수정", description = "문의 내용을 수정합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "성공"
    )
    public ResponseEntity<Void> updateInquiryStatus(
            @PathVariable(name = "inquiry-id") Long inquiryId, @RequestBody InquiryStatusUpdateRequest request
    ){
        inquiryService.updateInquiryStatus(inquiryId, request);
        return ResponseEntity.ok().build();
    }
}
