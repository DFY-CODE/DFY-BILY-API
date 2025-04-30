package one.dfy.bily.api.space.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import one.dfy.bily.api.common.dto.AmenityDto;
import one.dfy.bily.api.common.dto.AvailableUseDto;

import java.util.List;

@AllArgsConstructor
@Getter
public class SpaceListDto {

    @Schema(description = "콘텐츠 ID")
    private Long contentId;

    @Schema(description = "디스플레이 상태 (1: 노출, 0: 비노출)")
    private int displayStatus;

    @Schema(description = "공간 ID")
    private String spaceId;

    @Schema(description = "가격")
    private int price;

    @Schema(description = "면적(m²)")
    private double areaM2;

    @Schema(description = "공간명")
    private String name;

    @Schema(description = "작성자")
    private String author;

    @Schema(description = "편의 시설 목록")
    private List<AmenityDto> amenitiesList;

    @Schema(description = "이용 가능 목록")
    private List<AvailableUseDto> availableUsesList;

    @Schema(description = "편의 시설(문자열)")
    private String amenities;

    @Schema(description = "이용 가능 용도(문자열)")
    private String availableUses;

    @Schema(description = "조회수")
    private String views;

    public SpaceListDto(Long contentId, int displayStatus, String spaceId, int price, double areaM2, String name, String author, String amenities, String availableUses, String views) {
        this.contentId = contentId;
        this.displayStatus = displayStatus;
        this.spaceId = spaceId;
        this.price = price;
        this.areaM2 = areaM2;
        this.name = name;
        this.author = author;
        this.amenities = amenities;
        this.availableUses = availableUses;
        this.views = views;
    }

    public void updateAmenitiesList(List<AmenityDto> amenitiesList){
        this.amenitiesList = amenitiesList;
    }

    public void updateAvailableUses(List<AvailableUseDto> availableUses){
        this.availableUsesList = availableUsesList;
    }
}
