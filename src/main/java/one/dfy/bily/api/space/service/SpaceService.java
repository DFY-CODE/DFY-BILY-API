package one.dfy.bily.api.space.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.common.mapper.PaginationMapper;
import one.dfy.bily.api.space.mapper.SpaceDtoMapper;
import one.dfy.bily.api.space.model.*;
import one.dfy.bily.api.space.model.repository.*;
import one.dfy.bily.api.common.dto.*;
import one.dfy.bily.api.space.mapper.SpaceMapper;
import one.dfy.bily.api.space.dto.*;
import one.dfy.bily.api.util.S3Uploader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class SpaceService {
    private final SpaceMapper spaceMapper;
    private final ObjectMapper objectMapper;
    private final S3Uploader s3Uploader;
    private final SpaceRepository spaceRepository;
    private final SpaceFileInfoRepository spaceFileInfoRepository;
    private final SavedSpaceRepository savedSpaceRepository;
    private final AmenityRepository amenityRepository;
    private final AvailableUseRepository availableUseRepository;
    private final SpaceUseFileInfoRepository spaceUseFileInfoRepository;
    private final SpaceBlueprintFileInfoRepository spaceBlueprintFileInfoRepository;
    private final SpaceAmenityRepository spaceAmenityRepository;
    private final SpaceAvailableUseRepository spaceAvailableUseRepository;

    public SpaceNameInfoList findAllSpaceNames() {
        return new SpaceNameInfoList(
                spaceRepository.findAll().stream()
                .map(SpaceDtoMapper::toSpaceNameInfo)
                .toList()
        );
    }

    public AmenityInfoList findAmenityInfoList(){
        return new AmenityInfoList(
                amenityRepository.findByUsed(true).stream()
                .map(SpaceDtoMapper::toAmenityInfo)
                .toList()
        );
    }

    public AvailableUseList findAvailableUseList(){
        return new AvailableUseList(
                availableUseRepository.findByUsed(true).stream()
                        .map(SpaceDtoMapper::toAvailableUseInfo)
                        .toList()
        );
    }

    @Transactional
    public NonUserSpaceInfoResponse findNonUserSpaceInfoList(int page, int size){
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<Space> spaces = spaceRepository.findAll(pageable);

        List<Long> spaceIds = spaces.stream()
                .map(Space::getId)
                .toList();

        Map<Long, String> thumbnailUrlMap = findSpaceFileBySpaceIds(spaceIds);

        List<NonUserSpaceInfo> nonUserSpaceInfoList = SpaceDtoMapper.toNonUserSpaceInfoList(spaces.getContent(), thumbnailUrlMap);
        Pagination pagination = PaginationMapper.toPagination(pageable,spaces.getTotalElements(),spaces.getTotalPages());

        return new NonUserSpaceInfoResponse(nonUserSpaceInfoList, pagination);
    }

    @Transactional
    public UserSpaceInfoResponse findUserSpaceInfoList(int page, int size){
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<Space> spaces = spaceRepository.findAll(pageable);

        List<Long> spaceIds = spaces.stream()
                .map(Space::getId)
                .toList();

        Map<Long, String> thumbnailUrlMap = findSpaceFileBySpaceIds(spaceIds);

        List<UserSpaceInfo> nonUserSpaceInfoList = SpaceDtoMapper.toUserSpaceInfoList(spaces.getContent(), thumbnailUrlMap);
        Pagination pagination = PaginationMapper.toPagination(pageable,spaces.getTotalElements(),spaces.getTotalPages());

        return new UserSpaceInfoResponse(nonUserSpaceInfoList, pagination);
    }

    // 페이징 처리된 데이터 반환
    public SpaceListResponse getSpaces(int page, int size) {
        int offset = (page - 1) * size;
        List<SpaceListDto> spaces = spaceMapper.getSpaces(size, offset);

        for (SpaceListDto space : spaces) {
            // JSON 문자열을 List<Integer>로 변환
            List<Integer> amenityIds = parseJsonArray(space.getAmenities());

            // amenityList 리스트 설정
            List<AmenityDto> amenities = amenityIds.isEmpty() ? new ArrayList<>() : spaceMapper.selectAmenitiesByIds(amenityIds);
            space.updateAmenitiesList(amenities);

            // availableUseList 변환 및 설정
            List<Integer> useIds = parseJsonArray(space.getAvailableUses());
            List<AvailableUseDto> availableUses = useIds.isEmpty() ? new ArrayList<>() : spaceMapper.selectAvailableUsesByIds(useIds);
            space.updateAvailableUses(availableUses);
        }

        int totalCount = getTotalCount();
        Pagination pagination = new Pagination(page, size, (long) totalCount, (int) Math.ceil((double) totalCount / size));

        return new SpaceListResponse(spaces, pagination);
    }

    public List<SpaceDetailDto> getSpacesDetail(int contentId) {
        List<SpaceDetailDto> spaces = new ArrayList<>();
        SpaceDetailDto space = spaceMapper.findSpaceDetailById(contentId);

        if (space != null) {
            // Space File Info
            space.setSpaceFileList(spaceMapper.findSpaceFileInfoByContentId(contentId));
            // Space Use File Info
            space.setSpaceUseFileList(spaceMapper.findSpaceUseFileInfoByContentId(contentId));

            // Tags, Features 문자열 파싱
            if (space.getTags() != null && !space.getTags().isEmpty()) {
                space.setTags(Arrays.stream(space.getTags().toString().replaceAll("^\\[|\\]$", "").split(","))
                        .map(String::trim).collect(Collectors.toList()));
            } else {
                space.setTags(new ArrayList<>());
            }

            if (space.getFeatures() != null && !space.getFeatures().isEmpty()) {
                space.setFeatures(Arrays.stream(space.getFeatures().toString().replaceAll("^\\[|\\]$", "").split(","))
                        .map(String::trim).collect(Collectors.toList()));
            } else {
                space.setFeatures(new ArrayList<>());
            }

            // 편의시설 List
            List<AmenityDto> amenities = spaceMapper.selectAmenitiesList();
            space.setAmenitiesList(amenities);

            // 이용 가능 용도
            List<AvailableUseDto> availableUses = spaceMapper.selectAvailableUsesList();
            space.setAvailableUsesList(availableUses);

            spaces.add(space);
        }

        return spaces;
    }




    // JSON 배열을 List<Integer>로 변환하는 메서드
    private List<Integer> parseJsonArray(String jsonArray) {
        try {
            if (jsonArray == null || jsonArray.isEmpty() || jsonArray.equals("[]")) {
                return new ArrayList<>();
            }

            // 1단계: JSON을 List<String>으로 변환
            List<String> stringList = objectMapper.readValue(jsonArray, new TypeReference<List<String>>() {});

            // 2단계: String 리스트를 Integer 리스트로 변환
            List<Integer> intList = new ArrayList<>();
            for (String s : stringList) {
                intList.add(Integer.parseInt(s));
            }
            return intList;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    // JSON 배열을 List<String>으로 변환하는 메서드
    private List<String> parseJsonStringArray(String jsonArray) {
        try {
            if (jsonArray == null || jsonArray.isEmpty() || jsonArray.equals("[]")) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(jsonArray, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // 전체 공간의 총 갯수
    public int getTotalCount() {
        return spaceMapper.getTotalCount();
    }

    public AdminSpaceDto buildAdminSpaceDto(Long contentId, SpaceDetailRequest dto) {
        AdminSpaceDto adminSpaceDto = new AdminSpaceDto();

        // 요청 데이터 매핑
        adminSpaceDto.setContentId(contentId);
        adminSpaceDto.setDisplayStatus(dto.getDisplayStatus());
        adminSpaceDto.setFixedStatus(dto.getFixedStatus());
        adminSpaceDto.setSpaceId(dto.getSpaceId());
        adminSpaceDto.setPrice(dto.getPrice() != null && dto.getPrice() > 0 ? dto.getPrice() : 0); // 기본값 검증
        adminSpaceDto.setAreaM2(dto.getAreaM2() != null ? dto.getAreaM2() : BigDecimal.ZERO);      // 기본값 검증
        adminSpaceDto.setMaxCapacity(dto.getMaxCapacity() != null ? dto.getMaxCapacity() : 1);     // 기본값 검증
        adminSpaceDto.setDistrictInfo(dto.getDistrictInfo());
        adminSpaceDto.setLocation(dto.getLocation());
        adminSpaceDto.setName(dto.getName());
        adminSpaceDto.setTags(dto.getTags()); // JSON 변환 예시
        adminSpaceDto.setInfo(dto.getInfo());
        adminSpaceDto.setFeatures(dto.getFeatures());
        adminSpaceDto.setUsageTime(dto.getUsageTime());
        adminSpaceDto.setCancellationPolicy(dto.getCancellationPolicy());

        return adminSpaceDto;
    }

    private static String convertToJson(String tags) {
        if (tags == null || tags.isEmpty()) {
            return "[]"; // 기본 빈 JSON 배열
        }
        // String을 파싱하여 JSON으로 변환 (Jackson 사용 예시)
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(tags.split(",")); // 쉼표로 분리된 태그를 배열로 변환
        } catch (JsonProcessingException e) {
            // 로깅 및 기본값 반환
            return "[]";
        }
    }

    // 공간 이용 사례 이미지 order 정보
    public  void updateUseFileOrder(Long fileId, Integer order){
        spaceMapper.updateUseFileOrder(fileId, order);
    }

    // 공간 이미지 order 정보
    public  void updateFileOrder(Long fileId, Integer order){
        spaceMapper.updateFileOrder(fileId, order);
    }

    public Long getNextContentId() {
        return spaceMapper.getMaxContentId() + 1;
    }

    public void insertSpace(AdminSpaceDto spaceDto,
                            List<SpaceFileDto> spaceFileDtos,
                            List<SpaceUseFileDto> spaceUseFileDtos,
                            List<SpaceBulePrintFileDto> spaceBulePrintFileDtos) {
        // 공간 정보 삽입
        spaceMapper.insertSpace(spaceDto);

        // 공간 파일 정보 삽입
        if (spaceFileDtos != null && !spaceFileDtos.isEmpty()) {
            spaceFileDtos.forEach(spaceFileDto -> {
                spaceFileDto.setContentId(spaceDto.getContentId());
                spaceMapper.insertSpaceFile(spaceFileDto);
            });
        }

        // 공간 이용 파일 정보 삽입
        if (spaceUseFileDtos != null && !spaceUseFileDtos.isEmpty()) {
            spaceUseFileDtos.forEach(spaceUseFileDto -> {
                spaceUseFileDto.setContentId(spaceDto.getContentId());
                spaceMapper.insertSpaceUseFile(spaceUseFileDto);
            });
        }

        // 도면 파일 정보 삽입
        if (spaceBulePrintFileDtos != null && !spaceBulePrintFileDtos.isEmpty()) {
            spaceBulePrintFileDtos.forEach(spaceBulePrintFileDto -> {
                spaceBulePrintFileDto.setContentId(spaceDto.getContentId());
                spaceMapper.insertSpaceBulePrintFile(spaceBulePrintFileDto);
            });
        }
    }



    // 공간 정보 수정
    public void updateSpace(AdminSpaceDto spaceDto, List<SpaceFileDto> spaceFileDtos, List<SpaceUseFileDto> spaceUseFileDtos, List<SpaceBulePrintFileDto> spaceBulePrintFileDtos) {
        // 공간 정보 업데이트
        spaceMapper.updateSpace(spaceDto);

        // 공간 파일 정보 업데이트 또는 삽입
        if (spaceFileDtos != null && !spaceFileDtos.isEmpty()) {
            spaceFileDtos.forEach(spaceFileDto -> {
                spaceFileDto.setContentId(spaceDto.getContentId());
                if (spaceFileDto.getAttachFileId() == null) {
                    spaceMapper.insertSpaceFile(spaceFileDto);
                } else {
                    spaceMapper.updateSpaceFile(spaceFileDto);
                }
            });
        }

        // 공간 이용 파일 정보 업데이트 또는 삽입
        if (spaceUseFileDtos != null && !spaceUseFileDtos.isEmpty()) {
            spaceUseFileDtos.forEach(spaceUseFileDto -> {
                spaceUseFileDto.setContentId(spaceDto.getContentId());
                if (spaceUseFileDto.getAttachFileId() == null) {
                    spaceMapper.insertSpaceUseFile(spaceUseFileDto);
                } else {
                    spaceMapper.updateSpaceUseFile(spaceUseFileDto);
                }
            });
        }

        // 도면 정보 업데이트 또는 삽입
        if (spaceBulePrintFileDtos != null && !spaceBulePrintFileDtos.isEmpty()) {
            spaceBulePrintFileDtos.forEach(spaceBulePrintFileDto -> {
                spaceBulePrintFileDto.setContentId(spaceDto.getContentId());
                if (spaceBulePrintFileDto.getAttachFileId() == null) {
                    spaceMapper.insertSpaceBulePrintFile(spaceBulePrintFileDto);
                } else {
                    spaceMapper.updateSpaceBulePrintFile(spaceBulePrintFileDto);
                }
            });
        }

    }

    public Space findById(Long spaceId) {
        if (spaceId == null) {
            return null;
        }
        return spaceRepository.findById(spaceId).orElseThrow(() -> new IllegalArgumentException("공간이 존재하지 않습니다."));
    }

    @Transactional
    public Map<Long, String> findSpaceFileBySpaceIds(List<Long> spaceIdList) {
        String s3Url = s3Uploader.getSpaceS3Url();

        if (spaceIdList == null || spaceIdList.isEmpty()) {
            return Map.of();
        }

        return spaceFileInfoRepository.findBySpaceIdInAndUsedAndThumbnail(spaceIdList,true,true)
                .stream()
                .collect(Collectors.toMap(
                        SpaceFileInfo::getSpaceId,
                        file -> s3Url + file.getSaveFileName()
                ));
    }

    @Transactional
    public SpaceCommonResponse createSavedSpace(Long spaceId, Long userId) {
        Space space = findById(spaceId);

        savedSpaceRepository.findBySpaceAndUserId(space, userId)
                        .ifPresentOrElse(
                                existing -> {
                                    existing.updateUsed(true);
                                },
                                () -> savedSpaceRepository.save(new SavedSpace(space, userId))
                        );

        return new SpaceCommonResponse(true,"장소를 저장했습니다.");
    }

    @Transactional
    public SpaceCommonResponse cancelSavedSpace(Long spaceId, Long userId) {
        Space space = findById(spaceId);

        savedSpaceRepository.findBySpaceAndUserId(space, userId)
                .ifPresent(
                        existing -> {
                            existing.updateUsed(false);
                        }
                );

        return new SpaceCommonResponse(true,"장소를 저장을 취소했습니다.");
    }

    @Transactional(readOnly = true)
    public Page<SavedSpace> findSavedSpaceByUserId(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1 , size, Sort.by("createdAt").descending());

        return savedSpaceRepository.findByUserId(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Map<Long, String> findSpaceFileBySpaceList(List<SavedSpace> spaceList){
        List<Long> spaceIds = spaceList.stream()
                .map(SavedSpace::getSpace)
                .map(Space::getId)
                .toList();

        return findSpaceFileBySpaceIds(spaceIds);
    }

    public SpaceCommonResponse saveSpace(SpaceRequest request, List<MultipartFile> spaceImages,  List<MultipartFile> useCaseImages, MultipartFile blueprint ) {
        Space spaceEntity = spaceRepository.save(SpaceDtoMapper.toSpaceEntity(request));

        List<SpaceFileInfo> spaceFileInfos = IntStream.range(0, spaceImages.size())
                .mapToObj(i -> {
                    boolean isThumbnail = i == request.thumbnailIndex();
                    MultipartFile file = spaceImages.get(i);
                    FileUploadInfo uploadedFile = s3Uploader.spaceFileUpload(file);
                    return SpaceDtoMapper.toSpaceFileInfoEntity(uploadedFile, spaceEntity.getId(), i, isThumbnail);
                })
                .toList();

        spaceFileInfoRepository.saveAll(spaceFileInfos);

        List<SpaceUseFileInfo> spaceUseFileInfoList = IntStream.range(0, useCaseImages.size())
                .mapToObj(i -> {
                    MultipartFile file = spaceImages.get(i);
                    FileUploadInfo uploadedFile = s3Uploader.spaceFileUpload(file);
                    return SpaceDtoMapper.toSpaceUseFileInfoEntity(uploadedFile, spaceEntity.getId(), request.useCaseImageTitles().get(i),i);
                })
                .toList();

        spaceUseFileInfoRepository.saveAll(spaceUseFileInfoList);

        FileUploadInfo uploadedFile = s3Uploader.spaceFileUpload(blueprint);

        SpaceBlueprintFile spaceBlueprintFile = SpaceDtoMapper.toSpaceBlueprintFileInfoEntity(uploadedFile, spaceEntity.getId());

        spaceBlueprintFileInfoRepository.save(spaceBlueprintFile);

        List<SpaceAmenity> spaceAmenities = request.amenityList().stream()
                .map( amenity -> SpaceDtoMapper.toSpaceAmenityEntity(spaceEntity.getId(), amenity))
                .toList();

        spaceAmenityRepository.saveAll(spaceAmenities);

        List<SpaceAvailableUse> spaceAvailableUses = request.availableUseList().stream()
                .map( availableUse -> SpaceDtoMapper.toSpaceAvailableUseEntity(spaceEntity.getId(), availableUse))
                .toList();

        spaceAvailableUseRepository.saveAll(spaceAvailableUses);

        return new SpaceCommonResponse(true, "공간 생성이 완료되었습니다.");
    }

    public SpaceDetailInfo findSpaceDetailInfoBySpaceId(Long spaceId) {
        Space spaceEntity = findById(spaceId);
        String filePath = s3Uploader.getSpaceS3Url();


        List<Long> spaceAmenities = SpaceDtoMapper.spaceAmenityListToLongList(spaceAmenityRepository.findBySpaceId(spaceId));
        List<Long> spaceAvailableUses = SpaceDtoMapper.spaceAvailableUseListToLongList(spaceAvailableUseRepository.findBySpaceId(spaceId));

        List<SpaceFileInfoResponse> fileInfoResponseList = spaceFileInfoRepository.findBySpaceIdAndUsed(spaceId, true).stream()
                .map(file -> SpaceDtoMapper.toSpaceFileInfoResponse(file,filePath))
                .toList();

        List<SpaceUseFileResponse> useFileResponseList = spaceUseFileInfoRepository.findBySpaceIdAndUsed(spaceId, true).stream()
                .map(file -> SpaceDtoMapper.toSpaceUseFileResponse(file, filePath))
                .toList();

        Optional<SpaceBlueprintFile> optionalFile = spaceBlueprintFileInfoRepository.findBySpaceIdAndUsed(spaceId, true);

        SpaceBlueprintFileInfo spaceBlueprintFileInfo = optionalFile
                .map(file -> SpaceDtoMapper.toSpaceBlueprintFileInfo(file, s3Uploader.getSpaceS3Url() + file.getSaveFileName()))
                .orElse(null);


        return SpaceDtoMapper.toSpaceDetailInfo(spaceEntity, spaceAmenities, spaceAvailableUses, fileInfoResponseList, useFileResponseList, spaceBlueprintFileInfo);
    }

}
