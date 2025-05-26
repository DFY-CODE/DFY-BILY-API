package one.dfy.bily.api.space.dto;

import java.math.BigDecimal;

public record NonUserSpaceInfo(
        String spaceId,
        String thumbnailUrl,
        String spaceName,
        String districtInfo,
        BigDecimal areaM2,
        int areaPy
) {
}
