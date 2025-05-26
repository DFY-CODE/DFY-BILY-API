package one.dfy.bily.api.space.dto;

import java.util.List;
import java.util.stream.Collectors;

public record MapNonUserSpaceInfoList(
        List<MapNonUserSpaceInfo> mapNonUserSpaceInfoList
) {
    public MapNonUserSpaceInfoList(List<MapNonUserSpaceInfo> mapNonUserSpaceInfoList) {
        // mapNonUserSpaceInfoList의 각 요소를 변환하여 새로운 리스트 생성
        this.mapNonUserSpaceInfoList = mapNonUserSpaceInfoList.stream()
                .map(mapNonUserSpaceInfo -> new MapNonUserSpaceInfo(
                        mapNonUserSpaceInfo.spaceId() == null ? mapNonUserSpaceInfo.spaceName() : mapNonUserSpaceInfo.spaceId(),
                        mapNonUserSpaceInfo.thumbnailUrl(),
                        mapNonUserSpaceInfo.spaceName(),
                        mapNonUserSpaceInfo.districtInfo(),
                        mapNonUserSpaceInfo.areaM2(),
                        mapNonUserSpaceInfo.areaPy(),
                        mapNonUserSpaceInfo.latitude(),
                        mapNonUserSpaceInfo.longitude()
                ))
                .collect(Collectors.toList());
    }
}


