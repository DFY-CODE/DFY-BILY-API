package one.dfy.bily.api.common.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AdminSpaceDto {
    private Long contentId;
    private boolean displayStatus;
    private boolean fixedStatus;
    private String spaceId;
    private Double price;
    private BigDecimal areaM2;
    private int maxCapacity;
    private String districtInfo;
    private String location;
    private String name;

    private List<String> tags; // List<String> 형태로 변경
    private String info;
    private List<String> features; // List<String> 형태로 변경
/*    private List<String> images; // List<String> 형태로 변경
    private String usageCases;*/
    private String floorPlan;
    private String usageTime;
    private String cancellationPolicy;
    private LocalDateTime createdAt;
    private String author;

    private List<AmenityDto> amenitiesList;
    private List<AvailableUseDto> availableUsesList;

    private String amenities;  // JSON 문자열 그대로 유지
    private String availableUses;  // JSON 문자열 그대로 유지

    private String areaPy;
    private Double latitude;
    private Double longitude;
    private String views;
}
