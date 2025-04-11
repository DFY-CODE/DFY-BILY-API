package one.dfy.bily.api.common.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class SpaceDetailRequest {
    private Long contentId;
    private Boolean displayStatus;
    private Boolean fixedStatus;
    private String spaceId;
    private Double price;
    private BigDecimal areaM2;
    private Integer maxCapacity;
    private String districtInfo;
    private String location;
    private String name;
    private List<String> tags;
    private String info;
    private List<String> features;
    private String usageTime;
    private String cancellationPolicy;
    // ... 게터/세터 추가
}



