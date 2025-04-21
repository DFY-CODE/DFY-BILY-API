package one.dfy.bily.api.admin.model.rent;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "TBL_INQUIRY_FILE_INFO")
public class InquiryFileInfo {

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

    @Column(name = "DELETE_FLAG", length = 10)
    private String deleteFlag = "N";

    @Column(name = "CREATOR", nullable = false, length = 50)
    private String creator;

    @Column(name = "CREATE_DATE", nullable = false)
    private LocalDateTime createDate;

    @Column(name = "UPDATER", length = 50)
    private String updater;

    @Column(name = "UPDATE_DATE")
    private LocalDateTime updateDate;

    @Column(name = "FILE_TYPE", nullable = false, length = 50)
    private String fileType;
}
