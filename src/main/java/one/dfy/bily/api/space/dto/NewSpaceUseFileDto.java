package one.dfy.bily.api.space.dto;

import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

/** 새로 업로드되는 사용 사례 이미지의 부가 정보 */
@Getter
@Builder
public class NewSpaceUseFileDto {

    @Schema(description = "노출 순서(0부터)")
    private final int fileOrder;

    @Schema(description = "이미지 타이틀")
    private final String fileTitle;
}

