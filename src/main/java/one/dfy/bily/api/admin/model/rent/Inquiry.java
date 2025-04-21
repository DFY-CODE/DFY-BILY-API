package one.dfy.bily.api.admin.model.rent;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.dfy.bily.api.admin.dto.InquiryPreferredDate;
import one.dfy.bily.api.admin.dto.InquiryUpdateRequest;
import one.dfy.bily.api.admin.model.space.Space;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "TBL_INQUIRIES")
public class Inquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_Id")
    private Long inquiryId;

    @Column(name = "contact_person", nullable = false)
    private String contactPerson;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "position", nullable = false)
    private String position;

    @Column(name = "company_website", nullable = false)
    private String companyWebsite;

    @Column(name = "event_category")
    private String eventCategory;

    @Column(name = "event_name", nullable = false)
    private String eventName;

    @Column(name = "preferred_start_date", nullable = false)
    private LocalDateTime preferredStartDate;

    @Column(name = "preferred_end_date", nullable = false)
    private LocalDateTime preferredEndDate;

    @Column(name = "content")
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "author")
    private String author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", referencedColumnName = "CONTENT_ID", nullable = false)
    private Space space;

    @Column(name = "host_company", nullable = false)
    private String hostCompany;

    public void updateFrom(InquiryUpdateRequest request, Space space) {
        this.companyName = request.companyName();
        this.contactPerson = request.contactPerson();
        this.phoneNumber = request.phoneNumber();
        this.email = request.email();
        this.position = request.position();
        this.hostCompany = request.hostCompany();
        this.eventCategory = request.eventCategory();
        this.content = request.content();
        this.space = space;

        InquiryPreferredDate preferred = request.preferredDates().get(0);
        this.preferredStartDate = preferred.from();
        this.preferredEndDate = preferred.to();
    }


}
