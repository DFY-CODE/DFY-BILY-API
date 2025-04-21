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
import one.dfy.bily.api.admin.dto.InquiryResponse;
import one.dfy.bily.api.admin.dto.InquiryUpdateRequest;
import one.dfy.bily.api.admin.facade.InquiryFacade;
import one.dfy.bily.api.admin.service.InquiryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@Tag(name = "문의 관리 API", description = "문의 관리 관련 API")
@RequiredArgsConstructor
public class InquiryApiController {

    private final InquiryService inquiryService;
    private final InquiryFacade inquiryFacade;

    @GetMapping("/inquiry/list")
    @Operation(summary = "문의 리스트 조회", description = "검색 타입과 키워드를 통해 문의 리스트를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            externalValue = "/swagger/json/inquiry/findInquiryListByKeyword.json"
                    )
            ))
    })
    public ResponseEntity<List<InquiryResponse>> findInquiryListByKeyword(
            @Parameter(description = "문의 검색 타입", required = false) @RequestParam(value = "type", required = false) InquirySearchType type,
            @Parameter(description = "문의 검색 단어", required = false) @RequestParam(value = "keyword", required = false)String keyword,
            @Parameter(description = "문의 검색 페이지", required = false) @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "문의 검색 페이지 사이즈", required = false) @RequestParam(defaultValue = "20") int pageSize
    ) {
        return ResponseEntity.ok(inquiryService.findInquiryListByKeyword(type, keyword, page, pageSize));
    }

    @GetMapping("/inquiry/{inquiry-id}")
    @Operation(summary = "문의 상세 조회", description = "문의 아이디로 상세 데이터를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            externalValue = "/swagger/json/inquiry/findInquiryByInquiryId.json"
                    )
            ))
    })
    public ResponseEntity<InquiryResponse> findInquiryByInquiryId(@PathVariable(name = "inquiry-id") Long inquiryId) {
        return ResponseEntity.ok(inquiryService.findInquiryByInquiryId(inquiryId));
    }

    @PostMapping("/inquiry/{inquiry-id}/modify")
    @Operation(summary = "문의 상세 조회", description = "문의 아이디로 상세 데이터를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            externalValue = "/swagger/json/inquiry/findInquiryByInquiryId.json"
                    )
            ))
    })
    public ResponseEntity<InquiryResponse> updateInquiry(
            @PathVariable(name = "inquiry-id") Long inquiryId,
            @RequestBody InquiryUpdateRequest request
    ) {
        return ResponseEntity.ok(inquiryFacade.updateInquiry(inquiryId, request));
    }

}
