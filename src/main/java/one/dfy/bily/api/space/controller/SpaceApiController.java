package one.dfy.bily.api.space.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.dfy.bily.api.security.CustomUserDetails;
import one.dfy.bily.api.space.facade.SpaceFacade;
import one.dfy.bily.api.space.service.SpaceService;
import one.dfy.bily.api.space.dto.*;
import one.dfy.bily.api.util.AES256Util;
import one.dfy.bily.api.util.S3Uploader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/spaces")
@Tag(name = "공간관리 API", description = "공간관리 관련 API")
@RequiredArgsConstructor
public class SpaceApiController {

    // private final S3Uploader s3Uploader;
    private final SpaceService spaceService;
    private final SpaceFacade spaceFacade;

    @GetMapping("/amenity/all")
    @Operation(summary = "공간 편의시설 목록 조회", description = "공간 편의시설 목록 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SpaceListResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답 예시",
                                    externalValue = "/swagger/json/space/getSpaces.json"
                            )
                    )
            )
    })
    private ResponseEntity<AmenityInfoList> findAmenityInfoList(){
        return ResponseEntity.ok(spaceService.findAmenityInfoList());
    }

    @GetMapping("/available-use/all")
    @Operation(summary = "사용 가능 시설 목록 조회", description = "사용 가능 시설 목록 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SpaceListResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답 예시",
                                    externalValue = "/swagger/json/space/getSpaces.json"
                            )
                    )
            )
    })
    private ResponseEntity<AvailableUseList> findAvailableUseList(){
        return ResponseEntity.ok(spaceService.findAvailableUseList());
    }

    @GetMapping("/list/non-user")
    @Operation(summary = "비회원 공간 목록 조회", description = "페이지네이션된 공간 관리 목록 및 총 개수를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SpaceListResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답 예시",
                                    externalValue = "/swagger/json/space/getSpaces.json"
                            )
                    )
            )
    })
    public ResponseEntity<NonUserSpaceInfoResponse> findNonUserSpaceInfo(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(spaceService.findNonUserSpaceInfoList(page, size));
    }

    @GetMapping("/list")
    //@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "공간 목록 조회", description = "페이지네이션된 공간 관리 목록 및 총 개수를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SpaceListResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답 예시",
                                    externalValue = "/swagger/json/space/getSpaces.json"
                            )
                    )
            )
    })
    public ResponseEntity<UserSpaceInfoResponse> findUserSpaceInfoList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(spaceService.findUserSpaceInfoList(page, size));
    }

    @GetMapping("/list/admin")
    //@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "어드민 공간 목록 조회", description = "페이지네이션된 공간 관리 목록 및 총 개수를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SpaceListResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답 예시",
                                    externalValue = "/swagger/json/space/getSpaces.json"
                            )
                    )
            )
    })
    public ResponseEntity<AdminSpaceInfoList> findAdminSpaceInfoList(
            @RequestParam(required = false) String spaceAlias,
            @RequestParam(required = false) Boolean displayStatus,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(spaceFacade.findAdminSpaceInfoList(spaceAlias, displayStatus, page, pageSize));
    }

    @GetMapping("/list/map/non-user")
    //@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "비회원 공간 지도 목록 조회", description = "모든 공간의 목록을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SpaceListResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답 예시",
                                    externalValue = "/swagger/json/space/getSpaces.json"
                            )
                    )
            )
    })
    public ResponseEntity<MapNonUserSpaceInfoList> findMapNonSpaceInfoList() {
        return ResponseEntity.ok(spaceService.findMapNonUserSpaceInfoList());
    }



    @GetMapping("/list/map")
    //@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "공간 지도 목록 조회", description = "모든 공간의 목록을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SpaceListResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답 예시",
                                    externalValue = "/swagger/json/space/getSpaces.json"
                            )
                    )
            )
    })
    public ResponseEntity<MapUserSpaceInfoList> findMapSpaceInfoList() {
        return ResponseEntity.ok(spaceService.findMapUserSpaceInfoList());
    }

    @PostMapping("/save")
    @Operation(summary = "회원 공간 저장", description = "사용자가 공간을 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SpaceListResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답 예시",
                                    externalValue = "/swagger/json/space/getSpaces.json"
                            )
                    )
            )
    })
    public ResponseEntity<SpaceCommonResponse> createSavedSpace(
            @RequestBody SpaceId spaceId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest request

    ) throws Exception {
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

        //Long userId = 110L;
        return ResponseEntity.ok(spaceService.createSavedSpace(AES256Util.decrypt(spaceId.spaceId()), userId));
    }

    @PatchMapping("/save")
    @Operation(summary = "공간 저장 취소", description = "사용자가 공간을 저장을 취소합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SpaceListResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답 예시",
                                    externalValue = "/swagger/json/space/getSpaces.json"
                            )
                    )
            )
    })
    public ResponseEntity<SpaceCommonResponse> cancelSavedSpace(
            @RequestBody SpaceId spaceId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest request

    ) throws Exception {
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

//        Long userId = 110L;
        return ResponseEntity.ok(spaceService.cancelSavedSpace(AES256Util.decrypt(spaceId.spaceId()), userId));
    }

    @PostMapping(consumes = "multipart/form-data")
    @Operation(summary = "공간 생성", description = "사용자가 공간을 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SpaceCommonResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답 예시",
                                    externalValue = "/swagger/json/space/getSpaces.json"
                            )
                    )
            )
    })
    public ResponseEntity<SpaceResultResponse> saveSpace(
           /* @RequestPart("data") SpaceCreateRequest request,*/
            @RequestPart("data") String requestJson, // JSON 문자열로 수신
            @RequestPart("spaceFileInfoResponseList")                    // 필수
            List<MultipartFile> spaceImages,

            @RequestPart(value = "spaceUseFileResponseList", required = false)   // 선택
            List<MultipartFile> useCaseImages,

            @RequestPart(value = "spaceBlueprintFileUrl",   required = false)    // 선택
            MultipartFile blueprint,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest request

    ) throws JsonProcessingException {

        // JSON 문자열을 SpaceCreateRequest 객체로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        SpaceCreateRequest spaceCreateRequest = objectMapper.readValue(requestJson, SpaceCreateRequest.class);



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
        return ResponseEntity.ok(spaceService.saveSpace(spaceCreateRequest, spaceImages, useCaseImages, blueprint, userId));
    }

    @PatchMapping(consumes = "multipart/form-data")
    @Operation(summary = "공간 수정", description = "사용자가 공간을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SpaceCommonResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답 예시",
                                    externalValue = "/swagger/json/space/getSpaces.json"
                            )
                    )
            )
    })
    public ResponseEntity<SpaceCommonResponse> updateSpace(
            /*@RequestPart("data") SpaceUpdateRequest spaceUpdateRequest,*/
            /* 1) 기타 JSON 데이터 -------------------------------- */
            @RequestPart("data")             String requestJson,          // SpaceUpdateRequest JSON 문자열
            /* 2) 공간 이미지 ------------------------------------- */
            @RequestPart(value = "spaceFileInfoResponseList",      required = false)
            List<MultipartFile> spaceImages,

            /* 3) 사용 사례 이미지 -------------------------------- */
            @RequestPart(value = "spaceUseFileResponseList",       required = false)
            List<MultipartFile> useCaseImages,

            /* 4) 설계도 ------------------------------------------ */
            @RequestPart(value = "spaceBlueprintFileUrl",          required = false)
            MultipartFile blueprint,

            /* 5) 인증 ------------------------------------------- */

            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest request

    ) throws Exception {

        // JSON 문자열을 SpaceCreateRequest 객체로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        SpaceUpdateRequest spaceUpdateRequest = objectMapper.readValue(requestJson, SpaceUpdateRequest.class);

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

        //Long userId = 110L;
        return ResponseEntity.ok(spaceService.updateSpace(spaceUpdateRequest, spaceImages, useCaseImages, blueprint, userId));
    }

    @GetMapping("/name")
    @Operation(summary = "공간 이름 조회", description = "공간 이름 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SpaceNameInfoList.class),
                            examples = @ExampleObject(
                                    name = "성공 응답 예시",
                                    externalValue = "/swagger/json/space/getSpaces.json"
                            )
                    )
            )
    })
    public ResponseEntity<SpaceNameInfoList> findAllSpaceNames(){
        return ResponseEntity.ok(spaceService.findAllSpaceNames());
    }

    @GetMapping("/{id}")
    @Operation(summary = "공간 상세 조회", description = "공간 상세 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SpaceDetailInfo.class),
                            examples = @ExampleObject(
                                    name = "성공 응답 예시",
                                    externalValue = "/swagger/json/space/getSpaces.json"
                            )
                    )
            )
    })
    public ResponseEntity<SpaceDetailInfo> findSpaceDetailInfoBySpaceId(@PathVariable("id") String spaceId) throws Exception {

        SpaceDetailInfo result = spaceService.findSpaceDetailInfoBySpaceId(spaceId);

        log.info("findSpaceDetailInfoBySpaceId Result: {}", result);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/file/{saveFileName}")
    @Operation(summary = "파일 다운로드", description = "공간 상세 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SpaceDetailInfo.class),
                            examples = @ExampleObject(
                                    name = "성공 응답 예시",
                                    externalValue = "/swagger/json/space/getSpaces.json"
                            )
                    )
            )
    })
    public ResponseEntity<File> downloadSpaceFileAsTempFile(@PathVariable("saveFileName") String saveFileName) {
        return ResponseEntity.ok(spaceService.downloadSpaceFileAsTempFile(saveFileName));
    }


}
