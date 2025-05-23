package one.dfy.bily.api.space.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.dfy.bily.api.common.model.BaseEntity;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "TBL_SAVED_SPACE")
public class SavedSpace extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPACE_ID", referencedColumnName = "ID", nullable = false)
    private Space space;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "IS_USED")
    private boolean used;

    public SavedSpace(Space space, Long userId) {
        this.space = space;
        this.userId = userId;
        this.used = true;
    }

    public void updateUsed(boolean used) {
        this.used = used;
    }
}
