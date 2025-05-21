package one.dfy.bily.api.space.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.dfy.bily.api.security.CustomUserDetails;
import one.dfy.bily.api.space.service.SpaceService;
import one.dfy.bily.api.space.dto.*;
import one.dfy.bily.api.util.S3Uploader;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/spaces")
@Tag(name = "공간관리 API", description = "공간관리 관련 API")
@RequiredArgsConstructor
public class SpaceApiController {

    private final S3Uploader s3Uploader;
    private final SpaceService spaceService;

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
//    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
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

    @PostMapping("/save")
    @Operation(summary = "공간 저장", description = "사용자가 공간을 저장합니다.")
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
            @RequestBody SpaceId spaceId
//            @AuthenticationPrincipal CustomUserDetails userDetails
            ) {
//        Long userId = userDetails.getUserId();
        Long userId = 110L;
        return ResponseEntity.ok(spaceService.createSavedSpace(spaceId.spaceId(), userId));
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
            @RequestBody SpaceId spaceId
//            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
//        Long userId = userDetails.getUserId();
        Long userId = 110L;
        return ResponseEntity.ok(spaceService.cancelSavedSpace(spaceId.spaceId(), userId));
    }

    @PostMapping("")
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
    public ResponseEntity<SpaceCommonResponse> saveSpace(
            @RequestPart("data") SpaceRequest request,
            @RequestPart("spaceImages") List<MultipartFile> spaceImages,
            @RequestPart("useCaseImages") List<MultipartFile> useCaseImages,
            @RequestPart("blueprint") MultipartFile blueprint ) {
        return ResponseEntity.ok(spaceService.saveSpace(request, spaceImages, useCaseImages, blueprint));
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
    public ResponseEntity<SpaceDetailInfo> findSpaceDetailInfoBySpaceId(@PathVariable("id") Long spaceId) {
        return ResponseEntity.ok(spaceService.findSpaceDetailInfoBySpaceId(spaceId));
    }



}
