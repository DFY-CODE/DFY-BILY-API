package one.dfy.bily.api.space.dto;

import java.math.BigDecimal;
import java.util.List;

public record SpaceDetailInfo(
        Boolean displayStatus,
        Boolean fixedStatus,
        String spaceAlias,
        String location,
        Long price,
        BigDecimal areaM2,
        String districtInfo,
        String spaceName,
        String info,
        String features,
        String usageTime,
        String cancellationPolicy,
        List<Long> amenityList,
        List<Long> availableUseList,
        Integer areaPy,
        List<SpaceFileInfoResponse> spaceFileInfoResponseList,
        List<SpaceUseFileResponse> spaceUseFileResponseList,
        SpaceBlueprintFileInfo spaceBlueprintFileUrl
) {
}
