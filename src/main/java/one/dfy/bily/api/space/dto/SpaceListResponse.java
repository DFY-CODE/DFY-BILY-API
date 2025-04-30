package one.dfy.bily.api.space.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import one.dfy.bily.api.common.dto.Pagination;

import java.util.List;

public record SpaceListResponse(
        @Schema(description = "공간 리스트") List<SpaceListDto> spaces,
        @Schema(description = "페이징 정보") Pagination pagination
) {
}
