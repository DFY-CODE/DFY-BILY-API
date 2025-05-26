package one.dfy.bily.api.space.dto;

import java.math.BigDecimal;

public record MapNonUserSpaceInfo(
        String spaceId,
        String thumbnailUrl,
        String spaceName,
        String districtInfo,
        BigDecimal areaM2,
        int areaPy,
        BigDecimal latitude,
        BigDecimal longitude
) {
    public MapNonUserSpaceInfo(String spaceId,
                               String thumbnailUrl,
                               String spaceName,
                               String districtInfo,
                               BigDecimal areaM2,
                               int areaPy,
                               BigDecimal latitude,
                               BigDecimal longitude) {
        // spaceId를 id로 매핑. 입력 값이 null인 경우 id 값을 대체
        this.spaceId = (spaceId == null || spaceId.isBlank()) ? spaceName : spaceId;
        this.thumbnailUrl = thumbnailUrl;
        this.spaceName = spaceName;
        this.districtInfo = districtInfo;
        this.areaM2 = areaM2;
        this.areaPy = areaPy;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
