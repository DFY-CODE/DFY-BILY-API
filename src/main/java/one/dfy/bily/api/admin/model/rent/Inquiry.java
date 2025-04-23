package one.dfy.bily.api.admin.model.rent;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.dfy.bily.api.admin.dto.Inquiry.InquiryPreferredDate;
import one.dfy.bily.api.admin.dto.Inquiry.InquiryUpdateRequest;
import one.dfy.bily.api.admin.model.space.Space;
import one.dfy.bily.api.common.model.BaseEntity;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "TBL_INQUIRY")
public class Inquiry extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CONTACT_PERSON", nullable = false)
    private String contactPerson;

    @Column(name = "PHONE_NUMBER", nullable = false)
    private String phoneNumber;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "COMPANY_NAME", nullable = false)
    private String companyName;

    @Column(name = "POSITION", nullable = false)
    private String position;

    @Column(name = "COMPANY_WEBSITE", nullable = false)
    private String companyWebsite;

    @Column(name = "EVENT_CATEGORY")
    private String eventCategory;

    @Column(name = "EVENT_NAME", nullable = false)
    private String eventName;

    @Column(name = "PREFERRED_START_DATE", nullable = false)
    private LocalDateTime preferredStartDate;

    @Column(name = "PREFERRED_END_DATE", nullable = false)
    private LocalDateTime preferredEndDate;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "STATUS", nullable = false)
    private String status;

    @Column(name = "AUTHOR")
    private String author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTENT_ID", referencedColumnName = "CONTENT_ID", nullable = false)
    private Space space;

    @Column(name = "HOST_COMPANY", nullable = false)
    private String hostCompany;

    public void updateFrom(InquiryUpdateRequest request, Space space) {
        if (request.companyName() != null) this.companyName = request.companyName();
        if (request.contactPerson() != null) this.contactPerson = request.contactPerson();
        if (request.phoneNumber() != null) this.phoneNumber = request.phoneNumber();
        if (request.email() != null) this.email = request.email();
        if (request.position() != null) this.position = request.position();
        if (request.hostCompany() != null) this.hostCompany = request.hostCompany();
        if (request.eventCategory() != null) this.eventCategory = request.eventCategory();
        if (request.content() != null) this.content = request.content();
        if (space != null) this.space = space;

        if (request.preferredDates() != null && !request.preferredDates().isEmpty()) {
            InquiryPreferredDate preferred = request.preferredDates().get(0);
            if (preferred.from() != null) this.preferredStartDate = preferred.from();
            if (preferred.to() != null) this.preferredEndDate = preferred.to();
        }
    }
}