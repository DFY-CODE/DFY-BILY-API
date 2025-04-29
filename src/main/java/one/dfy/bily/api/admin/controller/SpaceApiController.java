package one.dfy.bily.api.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import one.dfy.bily.api.admin.dto.space.SpaceDetailDto;
import one.dfy.bily.api.admin.dto.space.SpaceDetailResponse;
import one.dfy.bily.api.admin.dto.space.SpaceListDto;
import one.dfy.bily.api.admin.dto.space.SpaceListResponse;
import one.dfy.bily.api.common.dto.*;
import one.dfy.bily.api.common.service.FileService;
import one.dfy.bily.api.admin.service.SpaceService;
import one.dfy.bily.api.common.service.UserService;
import one.dfy.bily.api.util.S3Uploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@Tag(name = "공간관리 API", description = "공간관리 관련 API")
public class SpaceApiController {

    @Autowired
    private S3Uploader s3Uploader;
    @Autowired
    private FileService fileService;
    @Autowired
    private UserService userService;
    @Autowired
    private SpaceService spaceService;



    @GetMapping("/spaces/list")
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
    public ResponseEntity<SpaceListResponse> getSpaces(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<SpaceListDto> spacesList = spaceService.getSpaces(page, size);
        int totalCount = spaceService.getTotalCount();



        return ResponseEntity.ok(new SpaceListResponse(spacesList,totalCount));
    }

    @GetMapping("/spaces/detail/{contentId}")
    @Operation(summary = "공간 상세 조회", description = "contentId를 통해 특정 공간의 세부 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SpaceDetailResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답 예시",
                                    externalValue = "/swagger/json/space/getSpaceDetail.json"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 공간 ID입니다.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류가 발생했습니다.",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<SpaceDetailResponse> getSpaceDetail(
            @Parameter(description = "상세 정보를 가져올 contentId", example = "1") @PathVariable int contentId) {

        List<SpaceDetailDto> SpaceDetailList = spaceService.getSpacesDetail(contentId);

        return ResponseEntity.ok(new SpaceDetailResponse(SpaceDetailList));
    }


    @PostMapping(value = "/spaces/insert", consumes = "multipart/form-data")
    @Operation(
            summary = "공간 정보를 삽입합니다.",
            description = "공간 정보 입력과 관련된 API. 공간 기본 정보, 태그, 이미지 및 도면 파일을 함께 업로드할 수 있습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공간 정보가 성공적으로 삽입됨",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Map<String, Object>> insertSpaceManager(
            @Parameter(description = "공간 표시 여부", required = false) @RequestParam(value = "displayStatus", required = false) boolean displayStatus,
            @Parameter(description = "공간 고정 여부", required = false) @RequestParam(value = "fixedStatus", required = false) boolean fixedStatus,
            @Parameter(description = "공간 ID", required = false) @RequestParam(value = "spaceId", required = false) String spaceId,
            @Parameter(description = "가격(1일 기준)", required = false) @RequestParam(value = "price", required = false) Double price,
            @Parameter(description = "면적(제곱미터)", required = false) @RequestParam(value = "areaM2", required = false) BigDecimal areaM2,
            @Parameter(description = "최대 수용 가능 인원", required = false) @RequestParam(value = "maxCapacity", required = false) Integer maxCapacity,
            @Parameter(description = "구/지역 정보", required = false) @RequestParam(value = "districtInfo", required = false) String districtInfo,
            @Parameter(description = "위치", required = false) @RequestParam(value = "location", required = false) String location,
            @Parameter(description = "공간 이름", required = false) @RequestParam(value = "name", required = false) String name,
            @Parameter(description = "공간 태그 (쉼표로 구분된 문자열)", required = false) @RequestParam(value = "tags", required = false) String tags,
            @Parameter(description = "공간 설명", required = false) @RequestParam(value = "info", required = false) String info,
            @Parameter(description = "공간 특징 (쉼표로 구분된 문자열)", required = false) @RequestParam(value = "features", required = false) String features,
            @Parameter(description = "이용 가능 시간 정보", required = false) @RequestParam(value = "usageTime", required = false) String usageTime,
            @Parameter(description = "취소 정책", required = false) @RequestParam(value = "cancellationPolicy", required = false) String cancellationPolicy,
            @Parameter(description = "편의 시설 정보", required = false) @RequestParam(value = "amenities", required = false) String amenities,
            @Parameter(description = "이용 가능 용도", required = false) @RequestParam(value = "availableUses", required = false) String availableUses,
            @Parameter(description = "면적 (평)", required = false) @RequestParam(value = "areaPy", required = false) String areaPy,
            @Parameter(description = "위도", required = false) @RequestParam(value = "latitude", required = false) Double latitude,
            @Parameter(description = "경도", required = false) @RequestParam(value = "longitude", required = false) Double longitude,
            @Parameter(description = "각 이미지 순서", required = false) @RequestParam(value = "spaceImageOrders", required = false) List<Integer> spaceImageOrders,
            @Parameter(description = "공간 이미지 파일", required = false) @RequestPart(value = "spaceImages", required = false) List<MultipartFile> spaceImages,
            @Parameter(description = "공간 이미지 제목", required = false) @RequestPart(value = "spaceImageTitles", required = false) List<String> spaceImageTitles,
            @Parameter(description = "사례 이미지 파일", required = false) @RequestPart(value = "useCaseImages", required = false) List<MultipartFile> useCaseImages,
            @Parameter(description = "사례 이미지 제목", required = false) @RequestPart(value = "useCaseImageTitles", required = false) List<String> useCaseImageTitles,
            @Parameter(description = "도면 이미지 파일", required = false) @RequestPart(value = "blueprint", required = false) List<MultipartFile> blueprint
    ) {

        try {
            List<String> featureList = features != null ? Arrays.asList(features.split(",")) : new ArrayList<>();
            List<String> tagList = tags != null ? Arrays.asList(tags.split(",")) : new ArrayList<>();

            // 새로운 contentId 생성 - TBL_SPACE에서 max(contentId) + 1 조회
            Long newContentId = spaceService.getNextContentId();


            // 공간 정보 DTO 생성
            AdminSpaceDto spaceDto = new AdminSpaceDto();
            spaceDto.setContentId(newContentId);
            spaceDto.setDisplayStatus(displayStatus);
            spaceDto.setFixedStatus(fixedStatus);
            spaceDto.setSpaceId(spaceId);
            spaceDto.setPrice(price);
            spaceDto.setAreaM2(areaM2);
            spaceDto.setMaxCapacity(maxCapacity);
            spaceDto.setDistrictInfo(districtInfo);
            spaceDto.setLocation(location);
            spaceDto.setName(name);
            spaceDto.setTags(tagList);
            spaceDto.setInfo(info);
            spaceDto.setFeatures(featureList);
            spaceDto.setUsageTime(usageTime);
            spaceDto.setCancellationPolicy(cancellationPolicy);
            spaceDto.setAmenities(amenities); // 문자열로 입력된 시설 정보
            spaceDto.setAvailableUses(availableUses); // 문자열로 입력된 이용 가능 용도
            spaceDto.setAreaPy(areaPy);
            spaceDto.setLatitude(latitude);
            spaceDto.setLongitude(longitude);

            // 공간 이미지 처리
            List<SpaceFileDto> spaceFileDtos = new ArrayList<>();
            if (spaceImages != null && !spaceImages.isEmpty()) {
                for (int i = 0; i < spaceImages.size(); i++) {
                    MultipartFile file = spaceImages.get(i);
                    String title = (spaceImageTitles != null && i < spaceImageTitles.size()) ? spaceImageTitles.get(i) : file.getOriginalFilename();
                    SpaceFileDto uploadedFile = s3Uploader.spaceUpload(newContentId, file.getSize(), file.getOriginalFilename(), file, "space", title);
                    uploadedFile.setFileOrder(spaceImageOrders != null && i < spaceImageOrders.size() ? spaceImageOrders.get(i) : (i + 1));
                    spaceFileDtos.add(uploadedFile);
                }
            }

            // 사례 이미지 처리
            List<SpaceUseFileDto> spaceUseFileDtos = new ArrayList<>();
            if (useCaseImages != null && !useCaseImages.isEmpty()) {
                for (int i = 0; i < useCaseImages.size(); i++) {
                    MultipartFile file = useCaseImages.get(i);
                    String title = (useCaseImageTitles != null && i < useCaseImageTitles.size()) ? useCaseImageTitles.get(i) : file.getOriginalFilename();
                    SpaceUseFileDto uploadedFile = s3Uploader.spaceUseUpload(newContentId, file.getSize(), file.getOriginalFilename(), file, "useCase", title);
                    uploadedFile.setFileOrder(i + 1);
                    spaceUseFileDtos.add(uploadedFile);
                }
            }

            // 도면 파일 처리
            List<SpaceBulePrintFileDto> blueprintFileDtos = new ArrayList<>();
            if (blueprint != null && !blueprint.isEmpty()) {
                for (MultipartFile file : blueprint) {
                    if (file != null && !file.isEmpty()) {
                        SpaceBulePrintFileDto uploadedFile = s3Uploader.blueprintUpload(
                                newContentId,
                                file.getSize(),
                                file.getOriginalFilename(),
                                file,
                                "blueprint",
                                file.getOriginalFilename()
                        );
                        blueprintFileDtos.add(uploadedFile);
                    }
                }
            }

            // 공간 정보 저장
            spaceService.insertSpace(spaceDto, spaceFileDtos, spaceUseFileDtos, blueprintFileDtos);

            return ResponseEntity.ok(Map.of("message", "공간 정보가 생성되었습니다."));
        } catch (IOException e) {
            log.error("파일 업로드 중 오류 발생:", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "파일 업로드 중 오류가 발생했습니다."));
        } catch (Exception e) {
            log.error("공간 정보 생성 중 오류 발생:", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "서버 오류가 발생했습니다."));
        }
    }

    @Operation(summary = "공간 상세 정보 수정", description = "공간 상세 정보를 수정하고, 이미지 파일도 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            value = "{\"message\": \"공간 정보가 수정되었습니다.\"}"
                    )
            )),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class),
                    examples = @ExampleObject(
                            name = "잘못된 요청 응답 예시",
                            value = "{\"error\": \"잘못된 요청입니다.\"}"
                    )
            )),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class),
                    examples = @ExampleObject(
                            name = "서버 오류 응답 예시",
                            value = "{\"error\": \"서버 오류가 발생했습니다.\"}"
                    )
            ))
    })

    @PostMapping(value = "/spaces/update/{contentId}", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> updateSpaceManager(
            @PathVariable Long contentId,
            @RequestParam(value = "displayStatus", required = false) boolean displayStatus,
            @RequestParam(value = "fixedStatus", required = false) boolean fixedStatus,
            @RequestParam(value = "spaceId", required = false) String spaceId,
            @RequestParam(value = "price", required = false) Double price,//가격 (1일 기준)
            @RequestParam(value = "areaM2", required = false) BigDecimal areaM2,//평
            @RequestParam(value = "maxCapacity", required = false) Integer maxCapacity,
            @RequestParam(value = "districtInfo", required = false) String districtInfo,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam(value = "info", required = false) String info,
            @RequestParam(value = "features", required = false) String features,
            @RequestParam(value = "usageTime", required = false) String usageTime,
            @RequestParam(value = "cancellationPolicy", required = false) String cancellationPolicy,
            @RequestParam(value = "amenities", required = false) String amenities, // 편의 시설 추가
            @RequestParam(value = "availableUses", required = false) String availableUses, // 이용 가능 용도 추가
            @RequestParam(value = "areaPy", required = false) String areaPy, //면적
            @RequestParam(value = "latitude", required = false) Double latitude,//위도
            @RequestParam(value = "longitude", required = false) Double longitude,//경도

            // 이미지 순서
            @RequestParam(value = "spaceImageOrders", required = false) List<Integer> spaceImageOrders,
            @RequestParam(value = "spaceImageIds", required = false) List<Long> spaceImageIds,
            @RequestParam(value = "useCaseImageOrders", required = false) List<Integer> useCaseImageOrders,
            @RequestParam(value = "useCaseImageIds", required = false) List<Long> useCaseImageIds,

            // 파일 업로드
            @RequestPart(value = "spaceImages", required = false) List<MultipartFile> spaceImages,
            @RequestPart(value = "spaceImageTitles", required = false) List<String> spaceImageTitles,
            @RequestPart(value = "useCaseImages", required = false) List<MultipartFile> useCaseImages,
            @RequestPart(value = "useCaseImageTitles", required = false) List<String> useCaseImageTitles,

            // 도면 파일 업로드
            @RequestPart(value = "blueprint", required = false) List<MultipartFile> blueprint

    ) {
        try {
            List<String> featureList = features != null ? Arrays.asList(features.split(",")) : new ArrayList<>();
            List<String> tagList = tags != null ? Arrays.asList(tags.split(",")) : new ArrayList<>();

            System.out.println("Features: " + featureList);
            System.out.println("Tags: " + tagList);


            // 1. 공간 정보 DTO 생성
            AdminSpaceDto spaceDto = new AdminSpaceDto();
            spaceDto.setContentId(contentId);
            spaceDto.setDisplayStatus(displayStatus);
            spaceDto.setFixedStatus(fixedStatus);
            spaceDto.setSpaceId(spaceId);
            spaceDto.setPrice(price);
            spaceDto.setAreaM2(areaM2);
            spaceDto.setMaxCapacity(maxCapacity);
            spaceDto.setDistrictInfo(districtInfo);
            spaceDto.setLocation(location);
            spaceDto.setName(name);
            spaceDto.setTags(tagList);
            spaceDto.setInfo(info);
            spaceDto.setFeatures(featureList);
            spaceDto.setUsageTime(usageTime);
            spaceDto.setCancellationPolicy(cancellationPolicy);
            // 추가된 부분: 편의 시설과 이용 가능 용도 설정
            spaceDto.setAmenities(amenities); // 문자열로 입력된 시설 정보
            spaceDto.setAvailableUses(availableUses); // 문자열로 입력된 이용 가능 용도
            spaceDto.setAreaPy(areaPy);
            spaceDto.setLatitude(latitude);
            spaceDto.setLongitude(longitude);

            // 1. **이미지 순서만 변경 처리**
            if (spaceImageIds != null && spaceImageOrders != null) {
                for (int i = 0; i < spaceImageIds.size(); i++) {
                    Long imageId = spaceImageIds.get(i);
                    Integer order = spaceImageOrders.get(i);
                    spaceService.updateFileOrder(imageId, order);
                }
            }

            if (useCaseImageIds != null && useCaseImageOrders != null) {
                for (int i = 0; i < useCaseImageIds.size(); i++) {
                    Long imageId = useCaseImageIds.get(i);
                    Integer order = useCaseImageOrders.get(i);
                    spaceService.updateUseFileOrder(imageId, order);
                }
            }


            // 공간 이미지 처리
            List<SpaceFileDto> spaceFileDtos = new ArrayList<>();
            if (spaceImages != null && !spaceImages.isEmpty()) {
                for (int i = 0; i < spaceImages.size(); i++) {
                    MultipartFile file = spaceImages.get(i);
                    String title = (spaceImageTitles != null && i < spaceImageTitles.size()) ? spaceImageTitles.get(i) : file.getOriginalFilename();
                    SpaceFileDto uploadedFile = s3Uploader.spaceUpload(contentId, file.getSize(), file.getOriginalFilename(), file, "space", title);
                    uploadedFile.setFileOrder(spaceImageOrders != null && i < spaceImageOrders.size() ? spaceImageOrders.get(i) : (i + 1));
                    spaceFileDtos.add(uploadedFile);
                }
            }

            // 사례 이미지 처리
            List<SpaceUseFileDto> spaceUseFileDtos = new ArrayList<>();
            if (useCaseImages != null && !useCaseImages.isEmpty()) {
                for (int i = 0; i < useCaseImages.size(); i++) {
                    MultipartFile file = useCaseImages.get(i);
                    String title = (useCaseImageTitles != null && i < useCaseImageTitles.size()) ? useCaseImageTitles.get(i) : file.getOriginalFilename();
                    SpaceUseFileDto uploadedFile = s3Uploader.spaceUseUpload(contentId, file.getSize(), file.getOriginalFilename(), file, "useCase", title);
                    uploadedFile.setFileOrder(useCaseImageOrders != null && i < useCaseImageOrders.size() ? useCaseImageOrders.get(i) : (i + 1));
                    spaceUseFileDtos.add(uploadedFile);
                }
            }

            // 도면 파일 처리
            List<SpaceBulePrintFileDto> blueprintFileDtos = new ArrayList<>();
            if (blueprint != null && !blueprint.isEmpty()) {
                for (MultipartFile file : blueprint) {
                    if (file != null && !file.isEmpty()) {
                        SpaceBulePrintFileDto uploadedFile = s3Uploader.blueprintUpload(
                                contentId,
                                file.getSize(),
                                file.getOriginalFilename(),
                                file,
                                "blueprint",
                                file.getOriginalFilename()
                        );
                        blueprintFileDtos.add(uploadedFile);
                    }
                }
            }


            // 공간 정보 업데이트
            spaceService.updateSpace(spaceDto, spaceFileDtos, spaceUseFileDtos, blueprintFileDtos);

            return ResponseEntity.ok(Map.of("message", "공간 정보가 수정되었습니다."));
        } catch (IOException e) {
            log.error("파일 업로드 중 오류 발생:", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "파일 업로드 중 오류가 발생했습니다."));
        } catch (Exception e) {
            log.error("공간 정보 업데이트 중 오류 발생:", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "서버 오류가 발생했습니다."));
        }
    }




}
