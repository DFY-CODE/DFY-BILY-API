package one.dfy.bily.api.user.dto;

import java.math.BigDecimal;
import java.util.List;

public record SavedSpaceInfo(
        Integer id,
        String spaceName,
        String location,
        BigDecimal areaM2,
        int areaPy,
        Long price,
        String tags,
        String thumbnail
) {
}
