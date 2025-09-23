package one.dfy.bily.api.space.service;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.dfy.bily.api.common.mapper.PaginationMapper;
import one.dfy.bily.api.space.mapper.SpaceDtoMapper;
import one.dfy.bily.api.space.model.*;
import one.dfy.bily.api.space.model.repository.*;
import one.dfy.bily.api.common.dto.*;
import one.dfy.bily.api.space.dto.*;
import one.dfy.bily.api.util.AES256Util;
import one.dfy.bily.api.util.FileValidationUtils;
import one.dfy.bily.api.util.S3Uploader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpaceService {
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

       /* // List<Space> spaces = spaceRepository.findAll();*/

        // ① 공개 공간만 조회
        List<Space> spaces = spaceRepository.findAllByDisplayStatusTrue();

        List<Long> spaceIds = spaces.stream()
                .map(Space::getId)
                .toList();

        Map<Long, String> thumbnailUrlMap = findSpaceFileBySpaceIds(spaceIds);

        List<MapNonUserSpaceInfo> nonUserSpaceInfoList = SpaceDtoMapper.toMapNonUserSpaceInfoList(spaces, thumbnailUrlMap);

        return new MapNonUserSpaceInfoList(nonUserSpaceInfoList);
    }

    @Transactional
    public MapUserSpaceInfoList findMapUserSpaceInfoList(){

        //List<Space> spaces = spaceRepository.findAll();

        // ① 공개 공간만 조회
        List<Space> spaces = spaceRepository.findAllByDisplayStatusTrue();

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

        return spaceFileInfoRepository
                .findBySpaceIdInAndThumbnail(spaceIdList, true) // used 조건 없이 조회
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


    public SpaceResultResponse saveSpace(SpaceCreateRequest request,
                                         List<MultipartFile> spaceImages,
                                         List<MultipartFile> useCaseImages,
                                         MultipartFile blueprint,
                                         Long userId) {

        /* 1. 기본 공간 저장 */
        Space spaceEntity = spaceRepository.save(
                SpaceDtoMapper.toSpaceEntity(request, userId)
        );

        /* ---------- 2. 공간 이미지 ---------- */
        // 메타데이터와 실제 파일 개수 검증
        List<SpaceImageMetaRequest> spaceImagesMeta = request.spaceImagesMeta();
        if (spaceImagesMeta == null || spaceImagesMeta.size() != spaceImages.size()) {
            throw new IllegalArgumentException("공간 이미지 메타데이터 개수가 업로드된 파일 개수와 다릅니다.");
        }

        List<SpaceFileInfo> spaceFileInfos =
                IntStream.range(0, spaceImages.size())
                        .mapToObj(i -> {
                            MultipartFile file = spaceImages.get(i);
                            SpaceImageMetaRequest meta = spaceImagesMeta.get(i);

                            FileUploadInfo uploadedFile = s3Uploader.spaceFileUpload(file);

                            // 메타데이터에서 썸네일 여부 · 정렬순서를 가져옴
                            boolean isThumbnail = Boolean.TRUE.equals(meta.thumbnail());
                            int fileOrder = meta.fileOrder() != null ? meta.fileOrder() : i;

                            return SpaceDtoMapper.toSpaceFileInfoEntity(
                                    uploadedFile,
                                    spaceEntity.getId(),
                                    fileOrder,
                                    isThumbnail
                            );
                        })
                        .toList();

        spaceFileInfoRepository.saveAll(spaceFileInfos);

        /* ---------- 3. 공간 이용 사례 이미지 ---------- */
        List<SpaceImageMetaRequest> useCaseImagesMeta = request.useCaseImagesMeta();
        if (useCaseImagesMeta == null || useCaseImagesMeta.size() != useCaseImages.size()) {
            throw new IllegalArgumentException("사용 사례 이미지 메타데이터 개수가 업로드된 파일 개수와 다릅니다.");
        }

        List<SpaceUseFileInfo> spaceUseFileInfoList =
                IntStream.range(0, useCaseImages.size())
                        .mapToObj(i -> {
                            MultipartFile file = useCaseImages.get(i);
                            SpaceImageMetaRequest meta = useCaseImagesMeta.get(i);

                            FileUploadInfo uploadedFile = s3Uploader.useCaseFileUpload(file);

                            int fileOrder = meta.fileOrder() != null ? meta.fileOrder() : i;
                            String fileTitle = meta.title() != null ? meta.title() : ("공간 이용 사례_" + i);

                            SpaceUseFileInfo useFileInfo = SpaceDtoMapper.toSpaceUseFileInfoEntity(
                                    uploadedFile,
                                    spaceEntity.getId(),
                                    fileTitle,
                                    fileOrder
                            );
                            useFileInfo.setCreator("admin");   // CREATOR 하드코딩
                            return useFileInfo;
                        })
                        .toList();

        spaceUseFileInfoRepository.saveAll(spaceUseFileInfoList);

        /* ---------- 4. 도면 이미지 ---------- */
        FileUploadInfo uploadedBlueprint = s3Uploader.bluePrintUpload(blueprint);
        SpaceBlueprintFile blueprintFile = SpaceDtoMapper.toSpaceBlueprintFileInfoEntity(
                uploadedBlueprint,
                spaceEntity.getId()
        );
        blueprintFile.setCreator("admin");
        spaceBlueprintFileInfoRepository.save(blueprintFile);

        /* ---------- 5. 편의시설 / 이용가능용도 ---------- */
        List<SpaceAmenity> spaceAmenities = request.amenityList().stream()
                .map(a -> SpaceDtoMapper.toSpaceAmenityEntity(spaceEntity.getId(), a))
                .toList();
        spaceAmenityRepository.saveAll(spaceAmenities);

        List<SpaceAvailableUse> spaceAvailableUses = request.availableUseList().stream()
                .map(u -> SpaceDtoMapper.toSpaceAvailableUseEntity(spaceEntity.getId(), u))
                .toList();
        spaceAvailableUseRepository.saveAll(spaceAvailableUses);

        /* ---------- 6. 결과 반환 ---------- */
        String resultSpaceId;
        try {
            resultSpaceId = AES256Util.encrypt(spaceEntity.getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return SpaceResultResponse.builder()
                .message("저장되었습니다.")
                .spaceId(resultSpaceId)
                .build();
    }

    private String extractFileName(String url) {
        try {
            return URLDecoder.decode(
                    url.substring(url.lastIndexOf('/') + 1).split("\\?")[0],
                    StandardCharsets.UTF_8
            );
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid file URL: " + url);
        }
    }

    @Transactional
    public SpaceCommonResponse updateSpace(SpaceUpdateRequest req,
                                           List<MultipartFile> spaceFiles,
                                           List<MultipartFile> useCaseFiles,
                                           MultipartFile blueprint,
                                           Long userId) throws Exception {

        // 1. 공간 엔티티 조회
        Long spaceId = AES256Util.decrypt(req.spaceId());
        Space space = findById(spaceId);

        // 2. 기본 정보 patch
        space.updateSpace( req.displayStatus(), req.fixedStatus(), req.spaceAlias(),
                req.location(), req.latitude(), req.longitude(),
                req.price(), req.areaM2(), req.districtInfo(),
                req.spaceName(), req.info(), req.features(),
                req.usageTime(), req.cancellationPolicy(), req.areaPy());


        spaceRepository.save(space);


        //==========================
        // ✅ 3-A. 공간 이미지 삭제
        //==========================
        this.deleteRemovedImages(req);

        //==========================
        // ✅ 4-A. 사용 사례 이미지 삭제
        //==========================
        this.deleteRemovedUseImages(req);

        //==========================
        // ✅ 5-A. 도면 이미지 삭제
        //==========================
        this.deleteRemovedBlueImages(req);

        /* ---------- 공간 이미지 동기화 ---------- */
        this.syncSpaceImages(spaceId, req, req.spaceImagesMeta(), spaceFiles);

        /* ---------- 사용 사례 이미지 동기화 ---------- */
            this.syncSpaceUseImages(spaceId, req, req.useCaseImagesMeta(), useCaseFiles);

        /* ---------- 도면(블루프린트) 동기화 ---------- */
        if (blueprint != null && !blueprint.isEmpty()) {
            syncSpaceBlueprintImage(spaceId, blueprint, "ADMIN");
        }

        // 5-1. 기존 행 제거
        spaceAmenityRepository.deleteBySpaceId(spaceId);
        spaceAvailableUseRepository.deleteBySpaceId(spaceId);

        // 5-2. 새로 삽입
        if (CollectionUtils.isNotEmpty(req.amenityList())) {
            List<SpaceAmenity> spaceAmenities = req.amenityList().stream()
                    .map(a -> SpaceDtoMapper.toSpaceAmenityEntity(spaceId, a))
                    .toList();
            spaceAmenityRepository.saveAll(spaceAmenities);
        }

        if (CollectionUtils.isNotEmpty(req.availableUseList())) {
            List<SpaceAvailableUse> spaceAvailableUses = req.availableUseList().stream()
                    .map(u -> SpaceDtoMapper.toSpaceAvailableUseEntity(spaceId, u))
                    .toList();
            spaceAvailableUseRepository.saveAll(spaceAvailableUses);
        }



        return new SpaceCommonResponse(true, "공간 수정이 완료되었습니다.");
    }

    /* ----------------------------------------------------------------------
     * 아래는 보조 메서드
     * ---------------------------------------------------------------------*/
    private void deleteRemovedImages(SpaceUpdateRequest req) {
        log.info("deleteRemovedImages:{}",req.deletedSpaceImages());
        // ===== 공간 이미지 =====
        if (CollectionUtils.isNotEmpty(req.deletedSpaceImages())) {
            for (String url : req.deletedSpaceImages()) {
                String fileName = extractFileName(url);
                spaceFileInfoRepository.findBySaveFileName(fileName).ifPresent(f -> {
                    s3Uploader.deleteFile(f.getSaveLocation(), f.getSaveFileName());
                    spaceFileInfoRepository.delete(f);
                });
            }
        }
    }

    private void deleteRemovedUseImages(SpaceUpdateRequest req) {
        log.info("deleteRemovedUseImages:{}",req.deletedSpaceImages());
        // ===== 이용사례 이미지 =====
        if (CollectionUtils.isNotEmpty(req.deletedUseCaseImages())) {
            for (String url : req.deletedUseCaseImages()) {
                String fileName = extractFileName(url);
                spaceUseFileInfoRepository.findBySaveFileName(fileName).ifPresent(file -> {
                    s3Uploader.deleteFile(file.getSaveLocation(), file.getSaveFileName());
                    spaceUseFileInfoRepository.delete(file);
                });
            }
        }
    }

    private void deleteRemovedBlueImages(SpaceUpdateRequest req) {
        log.info("deleteRemovedBlueImages:{}",req.deletedSpaceImages());
        // ===== 도면 이미지 =====
        if (CollectionUtils.isNotEmpty(req.deletedBlueprintImages())) {
            for (String url : req.deletedBlueprintImages()) {
                String fileName = extractFileName(url);
                spaceBlueprintFileInfoRepository.findBySaveFileName(fileName).ifPresent(file -> {
                    s3Uploader.deleteFile(file.getSaveLocation(), file.getSaveFileName());
                    spaceBlueprintFileInfoRepository.delete(file);
                });
            }
        }
    }

    /**
     * 공간 이미지 메타데이터만 갱신
     *  - 파일 업로드, S3 FileUpload
     */
    @Transactional
    public void syncSpaceImages(
            Long spaceId,
            SpaceUpdateRequest req,
            List<SpaceImageMetaRequest> metas,
            List<MultipartFile> spaceFiles
    ) {
        // 0) NPE 방지
        final List<SpaceImageMetaRequest> safeMetas = (metas == null) ? List.of() : metas;
        final List<MultipartFile> newFiles = (spaceFiles == null) ? List.of() : spaceFiles;

        // 0-1) 메타를 fileOrder 기준으로 정렬해서 처리 안정성 확보
        final List<SpaceImageMetaRequest> orderedMetas = safeMetas.stream()
                .sorted(Comparator.comparingInt(m -> {
                    Integer fo = m.fileOrder();
                    return (fo == null) ? Integer.MAX_VALUE : fo;
                }))
                .toList();

        // 1) 기존 이미지 로드
        List<SpaceFileInfo> existing = spaceFileInfoRepository
                .findBySpaceIdAndUsedOrderByFileOrderAsc(spaceId, true);

        Map<Long, SpaceFileInfo> existingById = existing.stream()
                .collect(Collectors.toMap(SpaceFileInfo::getId, Function.identity()));

        // 2) 메타에 포함되지 않은 기존 파일 찾기(삭제 대상)
        Set<Long> keepIds = orderedMetas.stream()
                .map(SpaceImageMetaRequest::fileId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<SpaceFileInfo> toDelete = existing.stream()
                .filter(e -> !keepIds.contains(e.getId()))
                .toList();

        if (!toDelete.isEmpty()) {
            List<String> keys = toDelete.stream()
                    .map(e -> e.getSaveLocation() + "/" + e.getSaveFileName())
                    .toList();
            s3Uploader.deleteFiles(keys);
            spaceFileInfoRepository.deleteAllInBatch(toDelete);
        }

        // 3) 신규 파일 매칭 커서
        int newFileCursor = 0;

        // 4) 메타 처리
        List<SpaceFileInfo> toUpdate = new ArrayList<>();

        for (SpaceImageMetaRequest meta : orderedMetas) {
            Long fileId = meta.fileId();

            // fileId가 null이거나 0(또는 음수)면 기존파일 갱신 로직을 타지 않도록 안전 처리
            if (fileId != null && fileId > 0) {
                SpaceFileInfo entity = existingById.get(fileId);
                if (entity == null) {
                    continue;
                }
                entity.setFileType(meta.fileType());
                entity.setFileOrder(meta.fileOrder());
                entity.setThumbnail(Boolean.TRUE.equals(meta.thumbnail()));
                entity.setUsed(true);
                toUpdate.add(entity);
            } else {
                // 신규 파일 등록(메타에는 fileId 없음)
                if (newFileCursor >= newFiles.size()) {
                    continue;
                }
                MultipartFile file = newFiles.get(newFileCursor++);
                FileUploadInfo uploaded = s3Uploader.spaceFileUpload(file);

                SpaceFileInfo entity = new SpaceFileInfo(
                        spaceId,
                        uploaded.originalFileName(),
                        uploaded.newFileName(),
                        uploaded.saveLocation(),
                        uploaded.fileSize(),
                        meta.fileType() != null ? meta.fileType() : file.getContentType(),
                        meta.fileOrder(),
                        Boolean.TRUE.equals(meta.thumbnail())
                );
                spaceFileInfoRepository.save(entity);
            }
        }


        // 5) 기존 엔티티의 Dirty Checking 반영을 보장하고 싶다면 saveAll/flush
        if (!toUpdate.isEmpty()) {
            spaceFileInfoRepository.saveAll(toUpdate);
        }
        spaceFileInfoRepository.flush();
    }

    /**
     * 이용사례 이미지 동기화
     * - 신규 파일이 있을 때만 S3 업로드 수행
     * - 기존 파일은 메타데이터만 갱신
     * - 메타에 포함되지 않은 기존 파일은 S3 및 DB에서 삭제
     */
    @Transactional
    public void syncSpaceUseImages(
            Long spaceId,
            SpaceUpdateRequest req,
            List<SpaceUseCaseImageMetaRequest> metas,
            List<MultipartFile> useFiles
    ) {
        // 0) NPE 방지
        final List<SpaceUseCaseImageMetaRequest> safeMetas = (metas == null) ? List.of() : metas;
        final List<MultipartFile> newFiles = (useFiles == null) ? List.of() : useFiles;

        // 0-1) 메타를 fileOrder 기준으로 정렬해서 처리 안정성 확보
        final List<SpaceUseCaseImageMetaRequest> orderedMetas = safeMetas.stream()
                .sorted(Comparator.comparingInt(m -> {
                    Integer fo = m.fileOrder();
                    return (fo == null) ? Integer.MAX_VALUE : fo;
                }))
                .toList();

        // 1) 기존 이미지 로드
        List<SpaceUseFileInfo> existing = spaceUseFileInfoRepository
                .findAllBySpaceIdAndUsedOrderByFileOrderAsc(spaceId, true);

        Map<Long, SpaceUseFileInfo> existingById = existing.stream()
                .collect(Collectors.toMap(SpaceUseFileInfo::getId, Function.identity()));

        // 2) 메타에 포함되지 않은 기존 파일 찾기(삭제 대상)
        Set<Long> keepIds = orderedMetas.stream()
                .map(SpaceUseCaseImageMetaRequest::fileId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<SpaceUseFileInfo> toDelete = existing.stream()
                .filter(e -> !keepIds.contains(e.getId()))
                .toList();

        if (!toDelete.isEmpty()) {
            List<String> keys = toDelete.stream()
                    .map(e -> e.getSaveLocation() + "/" + e.getSaveFileName())
                    .toList();
            s3Uploader.deleteFiles(keys);
            spaceUseFileInfoRepository.deleteAllInBatch(toDelete);
        }

        // 3) 신규 파일 매칭 커서: "신규 메타"의 상대 인덱스와 MultipartFile 리스트를 매칭
        int newFileCursor = 0;

        // 4) 메타 처리
        List<SpaceUseFileInfo> toUpdate = new ArrayList<>();

        for (SpaceUseCaseImageMetaRequest meta : orderedMetas) {
            Long fileId = meta.fileId();

            // 기존 파일의 경우 fileId가 반드시 존재하고, 0/음수 등의 잘못된 값도 차단
            if (fileId != null && fileId > 0) {
                SpaceUseFileInfo entity = existingById.get(fileId);
                if (entity == null) {
                    // 방어: 메타에 fileId가 있지만 DB에는 없는 경우 skip
                    continue;
                }
                entity.setFileTitle(meta.title());
                entity.setFileType(meta.fileType());
                entity.setFileOrder(meta.fileOrder());
                entity.setThumbnail(Boolean.TRUE.equals(meta.thumbnail()));
                entity.setUsed(true);
                toUpdate.add(entity);
            } else {
                // 신규 파일: 신규 메타 등장 순서대로 MultipartFile 매칭
                if (newFileCursor >= newFiles.size()) {
                    // 신규 메타 수 > 업로드 파일 수 인 경우 방어적으로 스킵
                    continue;
                }
                MultipartFile file = newFiles.get(newFileCursor++);
                FileUploadInfo uploaded = s3Uploader.useCaseFileUpload(file);

                SpaceUseFileInfo entity = new SpaceUseFileInfo(
                        spaceId,
                        uploaded.originalFileName(),
                        uploaded.newFileName(),          // S3에 저장된 파일명(Key)
                        uploaded.saveLocation(),
                        uploaded.fileSize(),
                        meta.fileType() != null ? meta.fileType() : file.getContentType(),
                        meta.fileOrder(),
                        true,
                        meta.title(),
                        Boolean.TRUE.equals(meta.thumbnail())
                );
                spaceUseFileInfoRepository.save(entity);
            }
        }


        // 5) 기존 엔티티의 Dirty Checking 반영을 보장하고 싶다면 saveAll/flush
        if (!toUpdate.isEmpty()) {
            spaceUseFileInfoRepository.saveAll(toUpdate);
        }
        spaceUseFileInfoRepository.flush();
    }


    /**
     * 도면(블루프린트) 파일 단건 동기화
     * 1) 기존 S3 객체 & DB 레코드 제거
     * 2) 새 파일 업로드 후 INSERT
     *
     * @param spaceId       대상 공간 ID
     * @param blueprintFile 새로 업로드할 MultipartFile
     * @param username      수정자(creator)
     * @return              저장된 도면 DTO
     */
    @Transactional
    public SpaceBlueprintFileInfo syncSpaceBlueprintImage(Long spaceId,
                                                          MultipartFile blueprintFile,
                                                          String username) {

        // 0. 기존 레코드 조회(최대 1건)
        spaceBlueprintFileInfoRepository.findFirstBySpaceId(spaceId).ifPresent(existing -> {
            // 0-1. S3 삭제
            s3Uploader.deleteFile(existing.getSaveLocation(), existing.getSaveFileName());
            // 0-2. DB 삭제
            spaceBlueprintFileInfoRepository.delete(existing);
        });

        // 1. 새 파일 업로드
        FileUploadInfo uploaded = s3Uploader.bluePrintUpload(blueprintFile);

        // 2. 엔티티 저장
        SpaceBlueprintFile entity = new SpaceBlueprintFile(
                spaceId,
                uploaded.originalFileName(),          // FILE_NAME
                uploaded.newFileName(),               // SAVE_FILE_NAME
                uploaded.saveLocation(),              // SAVE_LOCATION
                uploaded.fileSize(),                  // SAVE_SIZE
                blueprintFile.getContentType(),       // FILE_TYPE
                username                              // CREATOR
        );
        spaceBlueprintFileInfoRepository.save(entity);

        // 3. DTO 변환 & 반환
        return entity.toDto(uploaded.saveLocation());
    }

    public SpaceDetailInfo findSpaceDetailInfoBySpaceId(String spaceId) throws Exception {
        Long decSpaceId = AES256Util.decrypt(spaceId);
        Space spaceEntity = findById(decSpaceId);
        String filePath = s3Uploader.getSpaceS3Url();
        String useCasePath = s3Uploader.getuseCaseS3Url();
        String blueprintfilePath = s3Uploader.getBluePrintS3Url();


        List<Long> spaceAmenities = SpaceDtoMapper.spaceAmenityListToLongList(spaceAmenityRepository.findBySpaceId(decSpaceId));
        List<Long> spaceAvailableUses = SpaceDtoMapper.spaceAvailableUseListToLongList(spaceAvailableUseRepository.findBySpaceId(decSpaceId));

        List<SpaceFileInfoResponse> fileInfoResponseList = spaceFileInfoRepository.findBySpaceIdAndUsedOrderByFileOrderAsc(decSpaceId, true).stream()
                .map(file -> SpaceDtoMapper.toSpaceFileInfoResponse(file,filePath))
                .toList();

        List<SpaceUseFileResponse> useFileResponseList =
                spaceUseFileInfoRepository
                        .findAllBySpaceIdAndUsedOrderByFileOrderAsc(decSpaceId, true)  // ← 수정된 메서드
                        .stream()
                        .map(file -> SpaceDtoMapper.toSpaceUseFileResponse(file, useCasePath))
                        .toList();


        Optional<SpaceBlueprintFile> optionalFile = spaceBlueprintFileInfoRepository.findBySpaceIdAndUsed(decSpaceId, true);

        SpaceBlueprintFileInfo spaceBlueprintFileInfo = optionalFile
                //.map(SpaceDtoMapper::toSpaceBlueprintFileInfo)
                .map(file -> SpaceDtoMapper.toSpaceBlueprintFileInfo(file, blueprintfilePath))
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
            return spaceRepository.findByAliasContaining(spaceAlias, pageable);
        } else {
            return spaceRepository.findByAliasContainingAndDisplayStatus(spaceAlias, displayStatus, pageable);
        }
    }

    public File downloadSpaceFileAsTempFile(String saveFileName) {
        return s3Uploader.downloadSpaceFileAsTempFile(saveFileName);
    }

}
