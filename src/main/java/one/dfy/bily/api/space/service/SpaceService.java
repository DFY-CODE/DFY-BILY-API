package one.dfy.bily.api.space.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.common.mapper.PaginationMapper;
import one.dfy.bily.api.space.mapper.SpaceDtoMapper;
import one.dfy.bily.api.space.model.*;
import one.dfy.bily.api.space.model.repository.*;
import one.dfy.bily.api.common.dto.*;
import one.dfy.bily.api.space.mapper.SpaceMapper;
import one.dfy.bily.api.space.dto.*;
import one.dfy.bily.api.util.AES256Util;
import one.dfy.bily.api.util.S3Uploader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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

    @Transactional
    public MapNonUserSpaceInfoList findMapNonUserSpaceInfoList(){

        List<Space> spaces = spaceRepository.findAll();

        List<Long> spaceIds = spaces.stream()
                .map(Space::getId)
                .toList();

        Map<Long, String> thumbnailUrlMap = findSpaceFileBySpaceIds(spaceIds);

        List<MapNonUserSpaceInfo> nonUserSpaceInfoList = SpaceDtoMapper.toMapNonUserSpaceInfoList(spaces, thumbnailUrlMap);

        return new MapNonUserSpaceInfoList(nonUserSpaceInfoList);
    }

    @Transactional
    public MapUserSpaceInfoList findMapUserSpaceInfoList(){

        List<Space> spaces = spaceRepository.findAll();

        List<Long> spaceIds = spaces.stream()
                .map(Space::getId)
                .toList();

        Map<Long, String> thumbnailUrlMap = findSpaceFileBySpaceIds(spaceIds);

        List<MapUserSpaceInfo> userSpaceInfoList = SpaceDtoMapper.toMapUserSpaceInfoList(spaces, thumbnailUrlMap);

        return new MapUserSpaceInfoList(userSpaceInfoList);
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

        return savedSpaceRepository.findByUserIdAndUsed(userId, true, pageable);
    }

    @Transactional(readOnly = true)
    public Map<Long, String> findSpaceFileBySpaceList(List<SavedSpace> spaceList){
        List<Long> spaceIds = spaceList.stream()
                .map(SavedSpace::getSpace)
                .map(Space::getId)
                .toList();

        return findSpaceFileBySpaceIds(spaceIds);
    }

    public SpaceCommonResponse saveSpace(SpaceRequest request, List<MultipartFile> spaceImages,  List<MultipartFile> useCaseImages, MultipartFile blueprint, Long userId ) {
        Space spaceEntity = spaceRepository.save(SpaceDtoMapper.toSpaceEntity(request, userId));

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

    public SpaceDetailInfo findSpaceDetailInfoBySpaceId(String spaceId) throws Exception {
        Long decSpaceId = AES256Util.decrypt(spaceId);
        Space spaceEntity = findById(decSpaceId);
        String filePath = s3Uploader.getSpaceS3Url();


        List<Long> spaceAmenities = SpaceDtoMapper.spaceAmenityListToLongList(spaceAmenityRepository.findBySpaceId(decSpaceId));
        List<Long> spaceAvailableUses = SpaceDtoMapper.spaceAvailableUseListToLongList(spaceAvailableUseRepository.findBySpaceId(decSpaceId));

        List<SpaceFileInfoResponse> fileInfoResponseList = spaceFileInfoRepository.findBySpaceIdAndUsed(decSpaceId, true).stream()
                .map(file -> SpaceDtoMapper.toSpaceFileInfoResponse(file,filePath))
                .toList();

        List<SpaceUseFileResponse> useFileResponseList = spaceUseFileInfoRepository.findBySpaceIdAndUsed(decSpaceId, true).stream()
                .map(file -> SpaceDtoMapper.toSpaceUseFileResponse(file, filePath))
                .toList();

        Optional<SpaceBlueprintFile> optionalFile = spaceBlueprintFileInfoRepository.findBySpaceIdAndUsed(decSpaceId, true);

        SpaceBlueprintFileInfo spaceBlueprintFileInfo = optionalFile
                .map(SpaceDtoMapper::toSpaceBlueprintFileInfo)
                .orElse(null);


        return SpaceDtoMapper.toSpaceDetailInfo(spaceEntity, spaceAmenities, spaceAvailableUses, fileInfoResponseList, useFileResponseList, spaceBlueprintFileInfo);
    }

    public Page<Space> findAdminSpaceInfoList(String spaceAlias, Boolean displayStatus, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));

        if (spaceAlias == null && displayStatus == null) {
            return spaceRepository.findAll(pageable);
        } else if (spaceAlias == null) {
            return spaceRepository.findByDisplayStatus(displayStatus, pageable);
        } else if (displayStatus == null) {
            return spaceRepository.findByTitleContaining(spaceAlias, pageable);
        } else {
            return spaceRepository.findByTitleContainingAndDisplayStatus(spaceAlias, displayStatus, pageable);
        }
    }

    public File downloadSpaceFileAsTempFile(String saveFileName) {
        return s3Uploader.downloadSpaceFileAsTempFile(saveFileName);
    }

}
