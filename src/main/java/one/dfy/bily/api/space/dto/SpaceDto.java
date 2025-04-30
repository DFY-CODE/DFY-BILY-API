package one.dfy.bily.api.space.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import one.dfy.bily.api.common.dto.AmenityDto;
import one.dfy.bily.api.common.dto.AvailableUseDto;

@Data
public class SpaceDto {
    private Long contentId;
    private byte displayStatus;
    private byte fixedStatus;
    private String spaceId;
    private int price;
    private BigDecimal areaM2;
    private int maxCapacity;
    private String districtInfo;
    private String location;
    private String name;
    private String tags;
    private String info;
    private String features;
    private String images;
    private String usageCases;
    private String floorPlan;
    private String usageTime;
    private String cancellationPolicy;
    private LocalDateTime createdAt;
    private String author;
    private List<AmenityDto> amenities; // 리스트 형태로 변경
    private List<AvailableUseDto> availableUses; // 리스트 형태로 변경
    private String areaPy;
    private Double latitude;
    private Double longitude;
    private String views;
}

