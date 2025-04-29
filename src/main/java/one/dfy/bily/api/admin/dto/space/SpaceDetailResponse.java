package one.dfy.bily.api.admin.dto.space;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record SpaceDetailResponse(
        @Schema(description = "공간 리스트") List<SpaceDetailDto> spaces
) {
}
