package one.dfy.bily.api.inquiry.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.dfy.bily.api.inquiry.constant.InquiryStatus;
import one.dfy.bily.api.inquiry.dto.InquiryUpdateRequest;
import one.dfy.bily.api.space.model.Space;
import one.dfy.bily.api.common.constant.YesNo;
import one.dfy.bily.api.common.model.BaseEntity;

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

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private InquiryStatus status;

    @Column(name = "AUTHOR")
    private String author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTENT_ID", referencedColumnName = "CONTENT_ID", nullable = false)
    private Space space;

    @Column(name = "HOST_COMPANY", nullable = false)
    private String hostCompany;

    @Column(name = "IS_USED", nullable = false)
    private boolean isUsed;

    @Column(name = "USER_ID")
    private Long userId;

    public Inquiry(
            String contactPerson,
            String phoneNumber,
            String email,
            String companyName,
            String position,
            String companyWebsite,
            String eventCategory,
            String eventName,
            String content,
            InquiryStatus status,
            String author,
            Space space,
            String hostCompany,
            Long userId
    ) {
        this.contactPerson = contactPerson;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.companyName = companyName;
        this.position = position;
        this.companyWebsite = companyWebsite;
        this.eventCategory = eventCategory;
        this.eventName = eventName;
        this.content = content;
        this.status = status;
        this.author = author;
        this.space = space;
        this.hostCompany = hostCompany;
        this.isUsed = true;
        this.userId = userId;
    }

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
    }

    public void deleteInquiry() {
        isUsed = false;
    }

    public void updateStatus(InquiryStatus status) {
        this.status = status;
    }
}