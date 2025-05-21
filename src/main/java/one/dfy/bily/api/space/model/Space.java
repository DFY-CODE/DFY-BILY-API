package one.dfy.bily.api.space.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.dfy.bily.api.common.model.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "TBL_SPACE")
public class Space extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "DISPLAY_STATUS")
    private Boolean displayStatus;

    @Column(name = "FIXED_STATUS")
    private Boolean fixedStatus;

    @Column(name = "ALIAS")
    private String alias;

    @Column(name = "PRICE")
    private Long price;

    @Column(name = "AREA_M2", precision = 10, scale = 2)
    private BigDecimal areaM2;

    @Column(name = "DISTRICT_INFO")
    private String districtInfo;

    @Column(name = "LOCATION", nullable = false)
    private String location;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "INFO", length = 1000)
    private String info;

    @Column(name = "FEATURES")
    private String features;

    @Column(name = "USAGE_TIME")
    private String usageTime;

    @Column(name = "CANCELLATION_POLICY")
    private String cancellationPolicy;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "AREA_PY")
    private Integer areaPy;

    @Column(name = "LATITUDE", precision = 16, scale = 6)
    private BigDecimal latitude;

    @Column(name = "LONGITUDE", precision = 16, scale = 6)
    private BigDecimal longitude;

    @Column(name = "VIEWS")
    private Long views;

    @Column(name = "IS_USED")
    private Boolean used = true;

    @Column(name = "CREATOR", length = 50)
    private String creator;

    @Column(name = "UPDATER", length = 50)
    private String updater;

    @Column(name = "USER_ID")
    private Long userId;

    public Space(Boolean displayStatus, Boolean fixedStatus, String alias, Long price,
                 BigDecimal areaM2, String districtInfo, String location,
                 String title, String info, String features,
                 String usageTime, String cancellationPolicy,
                 Integer areaPy, BigDecimal latitude, BigDecimal longitude, Long views,
                 Boolean used, Long userId) {
        this.displayStatus = displayStatus;
        this.fixedStatus = fixedStatus;
        this.alias = alias;
        this.price = price;
        this.areaM2 = areaM2;
        this.districtInfo = districtInfo;
        this.location = location;
        this.title = title;
        this.info = info;
        this.features = features;
        this.usageTime = usageTime;
        this.cancellationPolicy = cancellationPolicy;
        this.areaPy = areaPy;
        this.latitude = latitude;
        this.longitude = longitude;
        this.views = views;
        this.used = used;
        this.userId = userId;
    }
}
