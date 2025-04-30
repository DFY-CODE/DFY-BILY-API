package one.dfy.bily.api.space.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SpaceImageUploadDTO {

    @Schema(description = "이미지 파일", type = "string", format = "binary")
    private MultipartFile file;

    @Schema(description = "이미지 제목", example = "풍경 사진 1")
    private String title;
}
