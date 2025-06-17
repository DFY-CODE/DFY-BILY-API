package one.dfy.bily.api.space.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record SpaceUseCaseImageMetaRequest(
        @Schema(description = "파일 ID (기존 이미지인 경우)") Long fileId,
        @Schema(description = "이용사례 이미지 제목")       String  title,
        @Schema(description = "정렬 순서")         Integer fileOrder,
        @Schema(description = "썸네일 여부")       Boolean thumbnail,
        @Schema(description = "기존 파일 S3 URL")       String  fileUrl,
        @Schema(description = "파일 MIME 타입")    String  fileType
) {}

