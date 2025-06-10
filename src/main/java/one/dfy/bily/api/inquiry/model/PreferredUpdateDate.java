package one.dfy.bily.api.inquiry.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.dfy.bily.api.inquiry.dto.InquiryPreferredDateInfo;
import one.dfy.bily.api.inquiry.dto.InquiryPreferredUpdateDateInfo;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "TBL_PREFERRED_DATE")
public class PreferredUpdateDate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INQUIRY_ID", referencedColumnName = "ID", nullable = false)
    private Inquiry inquiry;

    @Column(name = "PREFERENCE_LEVEL", nullable = false)
    private Integer preferenceLevel;

    @Column(name = "PREFERRED_START_DATE", nullable = false)
    private LocalDateTime preferredStartDate;

    @Column(name = "PREFERRED_END_DATE", nullable = false)
    private LocalDateTime preferredEndDate;

    public PreferredUpdateDate(Inquiry inquiry, Integer preferenceLevel, LocalDateTime preferredStartDate, LocalDateTime preferredEndDate) {
        this.inquiry = inquiry;
        this.preferenceLevel = preferenceLevel;
        this.preferredStartDate = preferredStartDate;
        this.preferredEndDate = preferredEndDate;
    }

/*    public boolean isSameLevel(InquiryPreferredUpdateDateInfo info) {
        return this.preferenceLevel.equals(info.preferenceLevel());
    }*/

    public void updateFromDto(InquiryPreferredUpdateDateInfo info) {
        this.preferredStartDate = info.from();
        this.preferredEndDate = info.to();
    }

    public boolean isSameLevel(InquiryPreferredUpdateDateInfo inquiryPreferredUpdateDateInfo) {
        return this.preferenceLevel.equals(inquiryPreferredUpdateDateInfo.preferenceLevel());
    }

}
