package one.dfy.bily.api.admin.dto.space;
import lombok.Data;
import one.dfy.bily.api.common.dto.AmenityDto;
import one.dfy.bily.api.common.dto.AvailableUseDto;
import one.dfy.bily.api.common.dto.SpaceFileInfoDto;
import one.dfy.bily.api.common.dto.SpaceUseFileInfoDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SpaceDetailDto {
    private int id;
    private boolean displayStatus;
    private boolean fixedStatus;
    private String spaceId;
    private int price;
    private double areaM2;
    private int maxCapacity;
    private String districtInfo;
    private String location;
    private String name;
    private List<String> tags;
    private String info;
    private List<String> features;

    private List<SpaceFileInfoDto> spaceFileList;
    private List<SpaceUseFileInfoDto> spaceUseFileList;

    private String floorPlan;
    private String usageTime;
    private String cancellationPolicy;
    private LocalDateTime createdAt;
    private String author;
    private List<AmenityDto> amenitiesList;
    private List<AvailableUseDto> availableUsesList;

    private String amenities;  // JSON 문자열 그대로 유지 data담기용도
    private String availableUses;  // JSON 문자열 그대로 유지 data 담기용도
    private int area_py;
    private double latitude;
    private double longitude;
    private int views;
}
