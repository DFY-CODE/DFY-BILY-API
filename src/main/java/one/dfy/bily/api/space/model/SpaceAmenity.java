package one.dfy.bily.api.space.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.dfy.bily.api.common.model.BaseEntity;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "TBL_SPACE_AMENITY")
public class SpaceAmenity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "SPACE_ID")
    private Long spaceId;

    @Column(name = "AMENITY_ID")
    private Long amenityId;

    public SpaceAmenity(Long spaceId, Long amenityId) {
        this.spaceId = spaceId;
        this.amenityId = amenityId;
    }
}
