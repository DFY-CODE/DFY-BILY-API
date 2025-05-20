package one.dfy.bily.api.space.dto;

import java.math.BigDecimal;

public record NonUserSpaceInfo(
        Long id,
        String thumbnailUrl,
        String title,
        String districtInfo,
        BigDecimal areaM2,
        int areaPy
) {
}
