package one.dfy.bily.api.user.dto;

import java.math.BigDecimal;

public record SavedSpaceInfo(
        Long id,
        String spaceId,
        String spaceName,
        String location,
        BigDecimal areaM2,
        int areaPy,
        Long price,
        String thumbnailUrl
) {
}
