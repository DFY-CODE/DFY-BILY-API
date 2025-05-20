package one.dfy.bily.api.space.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

public record SpaceRequest(
        @Schema(description = "공간 표시 여부")
        Boolean displayStatus,

        @Schema(description = "공간 고정 여부")
        Boolean fixedStatus,

        @Schema(description = "공간 ID")
        String spaceId,

        @Schema(description = "위치")
        String location,

        @Schema(description = "위도")
        Double latitude,

        @Schema(description = "경도")
        Double longitude,

        @Schema(description = "가격(1일 기준)")
        Long price,

        @Schema(description = "면적(제곱미터)")
        BigDecimal areaM2,

        @Schema(description = "구/지역 정보")
        String districtInfo,

        @Schema(description = "공간 이름")
        String name,

        @Schema(description = "공간 설명")
        String info,

        @Schema(description = "공간 특징 (쉼표로 구분된 문자열)")
        String features,

        @Schema(description = "이용 가능 시간 정보")
        String usageTime,

        @Schema(description = "취소 정책")
        String cancellationPolicy,

        @Schema(description = "편의 시설 정보")
        String amenities,

        @Schema(description = "이용 가능 용도")
        String availableUses,

        @Schema(description = "면적 (평)")
        String areaPy,

        @Schema(description = "사례 이미지 제목")
        List<String> useCaseImageTitles,

        @Schema(description = "대표 이미지 인덱스")
        int thumbnailIndex
) {}