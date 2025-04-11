package one.dfy.bily.api.common.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AdminSpaceListDto {
    private int contentId; // 컨텐츠 ID
    private byte displayStatus; //전시여부
    private String spaceId;// 공간Id
    private int price;//가격
    private BigDecimal areaM2;// 면적
    private String name;//타이틀

    private String author; //등록자

    private List<AmenityDto> amenitiesList;
    private List<AvailableUseDto> availableUsesList;

    private String amenities;  // JSON 문자열 그대로 유지
    private String availableUses;  // JSON 문자열 그대로 유지

    private String views; //조회수
}
