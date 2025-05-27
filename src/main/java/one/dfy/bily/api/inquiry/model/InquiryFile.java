package one.dfy.bily.api.inquiry.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.dfy.bily.api.common.model.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "TBL_INQUIRY_FILE_INFO")
public class InquiryFile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INQUIRY_ID", nullable = false)
    private Inquiry inquiry;

    @Column(name = "FILE_NAME", nullable = false)
    private String fileName;

    @Column(name = "SAVE_FILE_NAME", nullable = false)
    private String saveFileName;

    @Column(name = "SAVE_LOCATION", nullable = false)
    private String saveLocation;

    @Column(name = "SAVE_SIZE", nullable = false)
    private Long saveSize;

    @Column(name = "IS_USED", length = 10)
    private boolean isUsed = true;

    @Column(name = "CREATOR", length = 50)
    private Long creator;

    @Column(name = "UPDATER", length = 50)
    private Long updater;

    @Column(name = "FILE_TYPE", nullable = false, length = 50)
    private String fileType;

    public InquiryFile(
            Inquiry inquiry,
            String fileName,
            String saveFileName,
            String saveLocation,
            Long saveSize,
            Long creator,
            Long updater,
            String fileType
    ) {
        this.inquiry = inquiry;
        this.fileName = fileName;
        this.saveFileName = saveFileName;
        this.saveLocation = saveLocation;
        this.saveSize = saveSize;
        this.isUsed = true;
        this.creator = creator;
        this.updater = updater;
        this.fileType = fileType;
    }
}
