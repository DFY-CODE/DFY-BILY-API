package one.dfy.bily.api.space.mapper;

import one.dfy.bily.api.common.dto.FileUploadInfo;
import one.dfy.bily.api.space.dto.*;
import one.dfy.bily.api.space.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class SpaceDtoMapper {

    public static AmenityInfo toAmenityInfo(Amenity amenity) {
        return new AmenityInfo(amenity.getId(), amenity.getTitle());
    }

    public static AvailableUseInfo toAvailableUseInfo(AvailableUse availableUse) {
        return new AvailableUseInfo(availableUse.getId(), availableUse.getTitle());
    }

    public static List<UserSpaceInfo> toUserSpaceInfoList(List<Space> spaces, Map<Long, String> thumbnailUrlMap) {
        return spaces.stream()
                .map(space -> new UserSpaceInfo(
                        space.getId(),
                        thumbnailUrlMap.get(space.getId()),
                        space.getTitle(),
                        space.getDistrictInfo(),
                        space.getAreaM2(),
                        space.getAreaPy(),
                        space.getPrice()
                ))
                .toList();
    }

    public static List<NonUserSpaceInfo> toNonUserSpaceInfoList(List<Space> spaces, Map<Long, String> thumbnailUrlMap) {
        return spaces.stream()
                .map(space -> new NonUserSpaceInfo(
                        space.getId(),
                        thumbnailUrlMap.get(space.getId()),
                        space.getTitle(),
                        space.getDistrictInfo(),
                        space.getAreaM2(),
                        space.getAreaPy()
                ))
                .toList();
    }

    public static List<SpaceInfo> toSpaceInfoList(List<Space> spaces, Map<Long, String> thumbnailUrlMap) {
        return spaces.stream()
                .map(space -> new SpaceInfo(
                        space.getId(),
                        thumbnailUrlMap.get(space.getId()),
                        space.getTitle(),
                        space.getDistrictInfo(),
                        space.getAreaM2(),
                        space.getAreaPy(),
                        space.getPrice()
                ))
                .toList();
    }

    public static Space toSpaceEntity(SpaceRequest request) {
        return new Space(
                request.displayStatus(),
                request.fixedStatus(),
                request.spaceAlias(),
                request.price(),
                request.areaM2(),
                request.districtInfo(),
                request.location(),
                request.name(),
                request.info(),
                request.features(),
                request.usageTime(),
                request.cancellationPolicy(),
                request.areaPy() != null ? Integer.valueOf(request.areaPy()) : null,
                request.latitude() != null ? BigDecimal.valueOf(request.latitude()) : null,
                request.longitude() != null ? BigDecimal.valueOf(request.longitude()) : null,
                0L,
                true
        );
    }

    public static SpaceFileInfo toSpaceFileInfoEntity(FileUploadInfo file, Long spaceId, int idx, boolean isThumbnail) {
        return new SpaceFileInfo(
                spaceId,
                file.originalFileName(),
                file.newFileName(),
                file.saveLocation(),
                file.fileSize(),
                file.fileType(),
                idx,
                isThumbnail
        );
    }

    public static SpaceUseFileInfo toSpaceUseFileInfoEntity(FileUploadInfo file, Long spaceId, String fileTitle, int idx) {
        return new SpaceUseFileInfo(
                spaceId,
                file.originalFileName(),
                file.newFileName(),
                file.saveLocation(),
                file.fileSize(),
                file.fileType(),
                idx,
                fileTitle
        );
    }

    public static SpaceBlueprintFileInfo toSpaceBlueprintFileInfoEntity(FileUploadInfo file, Long spaceId) {
        return new SpaceBlueprintFileInfo(
                spaceId,
                file.originalFileName(),
                file.newFileName(),
                file.saveLocation(),
                file.fileSize(),
                file.fileType()
        );
    }

    public static SpaceAmenity toSpaceAmenityEntity(Long spaceId, Long amenityId) {
        return new SpaceAmenity(
                spaceId,
                amenityId
        );
    }

    public static SpaceAvailableUse toSpaceAvailableUseEntity(Long spaceId, Long availableUseId) {
        return new SpaceAvailableUse(
                spaceId,
                availableUseId
        );
    }
}
