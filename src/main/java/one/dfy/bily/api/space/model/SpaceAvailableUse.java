package one.dfy.bily.api.space.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "TBL_SPACE_USE_FILE_INFO")
public class SpaceAvailableUse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long spaceId;

    @Column
    private Long availableUseId;

    public SpaceAvailableUse(Long spaceId, Long availableUseId) {
        this.spaceId = spaceId;
        this.availableUseId = availableUseId;
    }
}
