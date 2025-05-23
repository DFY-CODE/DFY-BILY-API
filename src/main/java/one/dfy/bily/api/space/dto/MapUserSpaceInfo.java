package one.dfy.bily.api.space.dto;

import java.math.BigDecimal;

public record MapUserSpaceInfo(
        String id,
        String thumbnailUrl,
        String spaceName,
        String districtInfo,
        BigDecimal areaM2,
        int areaPy,
        Long price,
        BigDecimal latitude,
        BigDecimal longitude
) {
}
