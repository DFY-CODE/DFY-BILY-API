package one.dfy.bily.api.inquiry.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.dfy.bily.api.inquiry.constant.InquirySearchType;
import one.dfy.bily.api.inquiry.constant.InquiryStatus;
import one.dfy.bily.api.inquiry.dto.*;
import one.dfy.bily.api.inquiry.facade.InquiryFacade;
import one.dfy.bily.api.inquiry.service.InquiryService;
import one.dfy.bily.api.security.CustomUserDetails;
import one.dfy.bily.api.space.dto.SpaceDetailInfo;
import one.dfy.bily.api.space.dto.SpaceFileInfoResponse;
import one.dfy.bily.api.space.service.SpaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/inquiry")
@Tag(name = "문의 관리 API", description = "문의 관리 관련 API")
@RequiredArgsConstructor
public class InquiryApiController {

    private final InquiryService inquiryService;
    private final InquiryFacade inquiryFacade;
    private final SpaceService spaceService;


    @GetMapping()
    //@PreAuthorize("hasRole('ADMIN')")
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
    public ResponseEntity<InquiryListResponse> findInquiryListByKeywordAndDate(
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
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize,
            @Parameter(description = "문의 상태", required = false)
            @RequestParam(value = "status", required = false) List<InquiryStatus> statusList
    ) {
        return ResponseEntity.ok(inquiryService.findInquiryListByKeywordAndDate(type, keyword, startAt, endAt, page, pageSize, statusList));
    }

    @GetMapping("/{inquiry-id}")
    //@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
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
    public ResponseEntity<InquiryResponse> findInquiryByInquiryId(
            @PathVariable(name = "inquiry-id") Long inquiryId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest request

    ) {
        Long userId;
        boolean isAdmin = false;   // 기본값

        if (userDetails != null) {
            userId = userDetails.getUserId();

            // ROLE_ADMIN 권한 여부 판별
            isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        } else {
            // local 환경 + Origin 이 localhost:3001 일 때만 세팅된 값
            userId = (Long) request.getAttribute("FALLBACK_USER_ID");
            isAdmin = true;
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }

        // 1. 기본 InquiryResponse 가져오기
        InquiryResponse inquiryResponse = inquiryService.findInquiryByInquiryIdAndUserId(inquiryId, userId, isAdmin);

        // 2. 암호화된 spaceId 추출
        String encryptedSpaceId = inquiryResponse.spaceId();

        // 3. Space 상세 정보 조회
        SpaceDetailInfo spaceDetailInfo = null;

        try {
            spaceDetailInfo = spaceService.findSpaceDetailInfoBySpaceId(encryptedSpaceId);
        } catch (Exception e) {
            throw new RuntimeException("공간 정보 조회 중 오류가 발생했습니다.", e);
        }

        // 4. 추가된 공간 정보를 InquiryResponse로 매핑
        InquiryResponse updatedResponse = new InquiryResponse(
                inquiryResponse.id(),
                inquiryResponse.spaceId(),
                inquiryResponse.contactPerson(),
                inquiryResponse.phoneNumber(),
                inquiryResponse.email(),
                inquiryResponse.companyName(),
                inquiryResponse.position(),
                inquiryResponse.companyWebsite(),
                inquiryResponse.eventCategory(),
                inquiryResponse.preferredDates(),
                inquiryResponse.content(),
                inquiryResponse.fileAttachment(),
                inquiryResponse.createdAt(),
                inquiryResponse.status(),
                inquiryResponse.hostCompany(),
                inquiryResponse.spaceIdName(),
                spaceDetailInfo != null ? spaceDetailInfo.spaceName() : null, // 공간 이름
                spaceDetailInfo != null ? getThumbnailUrl(spaceDetailInfo) : null, // 썸네일 URL
                spaceDetailInfo != null ? spaceDetailInfo.spaceAlias() : null  // 공간 별칭
        );


        // 5. 최종 ResponseEntity로 반환
        return ResponseEntity.ok(updatedResponse);



//        return ResponseEntity.ok(inquiryService.findInquiryByInquiryIdAndUserId(inquiryId, userId, isAdmin));
    }

    // 썸네일 URL 추출 메서드
    private String getThumbnailUrl(SpaceDetailInfo spaceDetailInfo) {
        return spaceDetailInfo.spaceFileInfoResponseList().stream()
                .filter(SpaceFileInfoResponse::isThumbnail)
                .map(SpaceFileInfoResponse::fileUrl)
                .findFirst()
                .orElse(null);
    }


    @PostMapping(consumes = "multipart/form-data")
    //@PreAuthorize("hasRole('USER')")
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
    public ResponseEntity<InquiryResponse> createInquiry(
            @RequestPart("data") String jsonData,
            @RequestPart("attachFileList") List<MultipartFile> fileAttachments,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest request

    ) {
        try {

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new Jdk8Module());
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // 알 수 없는 필드 무시
            objectMapper.findAndRegisterModules();

            InquiryCreateRequest inquiryCreateRequest = objectMapper.readValue(jsonData, InquiryCreateRequest.class);

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

            return ResponseEntity.ok(inquiryFacade.createInquiry(inquiryCreateRequest, fileAttachments, userId));
        } catch (Exception e) {
            // JSON 파싱 오류 처리
            return ResponseEntity.badRequest().build();
        }

    }

    @PatchMapping("/{inquiry-id}")
    //@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
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
