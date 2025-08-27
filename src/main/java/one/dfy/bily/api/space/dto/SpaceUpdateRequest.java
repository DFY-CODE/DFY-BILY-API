package one.dfy.bily.api.space.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

public record SpaceUpdateRequest(

        /* ---------------- 기본 정보 ---------------- */
        @Schema(description = "공간 아이디")
        String spaceId,
        @Schema(description = "공간 표시 여부")
        Boolean displayStatus,
        @Schema(description = "공간 고정 여부")
        Boolean fixedStatus,
        @Schema(description = "공간 별칭")
        String spaceAlias,
        @Schema(description = "위치")
        String location,
        @Schema(description = "위도")
        BigDecimal latitude,
        @Schema(description = "경도")
        BigDecimal longitude,
        @Schema(description = "가격(1일 기준)")
        Long price,
        @Schema(description = "면적(제곱미터)")
        BigDecimal areaM2,
        @Schema(description = "면적 (평)")
        Integer areaPy,
        @Schema(description = "구/지역 정보")
        String districtInfo,
        @Schema(description = "공간 이름")
        String spaceName,
        @Schema(description = "공간 설명")
        String info,
        @Schema(description = "공간 특징(쉼표로 구분)")
        String features,
        @Schema(description = "이용 가능 시간")
        String usageTime,
        @Schema(description = "취소 정책")
        String cancellationPolicy,
        @Schema(description = "편의 시설 ID 목록")
        List<Long> amenityList,
        @Schema(description = "이용 가능 용도 ID 목록")
        List<Long> availableUseList,

        /* ---------------- 새로 추가된 메타 필드 ---------------- */
        @Schema(description = "공간 이미지 메타데이터 목록")
        List<SpaceImageMetaRequest> spaceImagesMeta,

        @Schema(description = "사용 사례 이미지 메타데이터 목록")
        List<SpaceUseCaseImageMetaRequest> useCaseImagesMeta,

        List<String> newSpaceImages,

        List<String> newUseCaseImages,

        List<String> newBlueprintImages,

        List<String> deletedSpaceImages,

        List<String> deletedUseCaseImages,

        List<String> deletedBlueprintImages



) {}
