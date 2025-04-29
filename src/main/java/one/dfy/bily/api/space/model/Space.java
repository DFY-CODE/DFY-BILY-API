package one.dfy.bily.api.space.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "TBL_SPACE")
public class Space {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONTENT_ID")
    private Integer contentId;

    @Column(name = "DISPLAY_STATUS")
    private Boolean displayStatus;

    @Column(name = "FIXED_STATUS")
    private Boolean fixedStatus;

    @Column(name = "SPACE_ID")
    private String spaceId;

    @Column(name = "PRICE")
    private Integer price;

    @Column(name = "AREA_M2", precision = 10, scale = 2)
    private BigDecimal areaM2;

    @Column(name = "MAX_CAPACITY")
    private Integer maxCapacity;

    @Column(name = "DISTRICT_INFO")
    private String districtInfo;

    @Column(name = "LOCATION", nullable = false)
    private String location;

    @Column(name = "NAME")
    private String name;

    @Column(name = "TAGS")
    private String tags;

    @Column(name = "INFO", length = 1000)
    private String info;

    @Column(name = "FEATURES")
    private String features;

    @Column(name = "FLOOR_PLAN")
    private String floorPlan;

    @Column(name = "USAGE_TIME")
    private String usageTime;

    @Column(name = "CANCELLATION_POLICY")
    private String cancellationPolicy;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "AUTHOR")
    private String author;

    @Column(name = "AMENITIES")
    private String amenities;

    @Column(name = "AVAILABLE_USES")
    private String availableUses;

    @Column(name = "AREA_PY")
    private String areaPy;

    @Column(name = "LATITUDE", precision = 16, scale = 6)
    private BigDecimal latitude;

    @Column(name = "LONGITUDE", precision = 16, scale = 6)
    private BigDecimal longitude;

    @Column(name = "VIEWS")
    private String views;

    @Column(name = "CREATOR", length = 50)
    private String creator;

    @Column(name = "CREATE_DT")
    private LocalDateTime createDt;

    @Column(name = "UPDATER", length = 50)
    private String updater;

    @Column(name = "UPDATE_DT")
    private LocalDateTime updateDt;
}
