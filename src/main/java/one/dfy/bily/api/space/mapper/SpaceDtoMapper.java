package one.dfy.bily.api.space.mapper;

import one.dfy.bily.api.space.dto.AmenityInfo;
import one.dfy.bily.api.space.dto.NonUserSpaceInfo;
import one.dfy.bily.api.space.dto.SpaceInfo;
import one.dfy.bily.api.space.dto.UserSpaceInfo;
import one.dfy.bily.api.space.model.Amenity;
import one.dfy.bily.api.space.model.Space;

import java.util.List;
import java.util.Map;

public class SpaceDtoMapper {
    public static AmenityInfo toAmenityInfo(Amenity amenity) {
        return new AmenityInfo(amenity.getId(), amenity.getTitle());
    }

    public static List<UserSpaceInfo> toUserSpaceInfoList(List<Space> spaces, Map<Long, String> thumbNailUrlMap) {
        return spaces.stream()
                .map(space -> new UserSpaceInfo(
                        space.getId(),
                        thumbNailUrlMap.get(space.getId()),
                        space.getTitle(),
                        space.getDistrictInfo(),
                        space.getAreaM2(),
                        space.getAreaPy(),
                        space.getPrice()
                ))
                .toList();
    }

    public static List<NonUserSpaceInfo> toNonUserSpaceInfoList(List<Space> spaces, Map<Long, String> thumbNailUrlMap) {
        return spaces.stream()
                .map(space -> new NonUserSpaceInfo(
                        space.getId(),
                        thumbNailUrlMap.get(space.getId()),
                        space.getTitle(),
                        space.getDistrictInfo(),
                        space.getAreaM2(),
                        space.getAreaPy()
                ))
                .toList();
    }

    public static List<SpaceInfo> toSpaceInfoList(List<Space> spaces, Map<Long, String> thumbNailUrlMap) {
        return spaces.stream()
                .map(space -> new SpaceInfo(
                        space.getId(),
                        thumbNailUrlMap.get(space.getId()),
                        space.getTitle(),
                        space.getDistrictInfo(),
                        space.getAreaM2(),
                        space.getAreaPy(),
                        space.getPrice()
                ))
                .toList();
    }
}
