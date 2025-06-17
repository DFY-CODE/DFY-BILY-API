package one.dfy.bily.api.space.dto;

import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

/** 새로 업로드되는 공간 이미지의 부가 정보 */
@Getter
@Builder
public class NewSpaceFileDto {

    @Schema(description = "노출 순서(0부터)")
    private final int fileOrder;

}

