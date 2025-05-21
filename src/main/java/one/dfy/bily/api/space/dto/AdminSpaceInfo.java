package one.dfy.bily.api.space.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AdminSpaceInfo (
        Long id,
        LocalDateTime createdAt,
        String spaceAlias,
        String spaceName,
        BigDecimal areaM2,
        Integer areaPy,
        Long price,
        boolean displayStatus,
        String userName
) {
}
