package one.dfy.bily.api.memo.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.dfy.bily.api.common.model.BaseEntity;
import one.dfy.bily.api.memo.constant.MemoType;
import one.dfy.bily.api.user.model.User;

@Entity
@Table(name = "TBL_MEMO")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Memo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private MemoType type;

    @Column(name = "CONTENT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID", nullable = false)
    private User user;

    @Column(name = "INQUIRY_ID")
    private Long inquiryId;

    @Column(name = "IS_USED", nullable = false)
    private boolean used = true;

    public Memo(MemoType type, String content, User user, Long inquiryId) {
        this.type = type;
        this.content = content;
        this.user = user;
        this.inquiryId = inquiryId;
    }

    public void deleteMemo(){
        this.used = false;
    }
}
