package one.dfy.bily.api.space.dto;

import java.math.BigDecimal;

public record MapNonUserSpaceInfo(
        String id,
        String thumbnailUrl,
        String spaceName,
        String districtInfo,
        BigDecimal areaM2,
        int areaPy,
        BigDecimal latitude,
        BigDecimal longitude
) {
}
