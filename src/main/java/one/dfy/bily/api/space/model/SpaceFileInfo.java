package one.dfy.bily.api.space.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.dfy.bily.api.common.dto.FileUploadInfo;
import one.dfy.bily.api.common.model.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "TBL_SPACE_FILE_INFO")
public class SpaceFileInfo extends BaseEntity {

    /* ---------- 컬럼 ---------- */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "SPACE_ID", nullable = false)
    private Long spaceId;

    @Column(name = "FILE_NAME", nullable = false)
    private String fileName;

    @Column(name = "SAVE_FILE_NAME")
    private String saveFileName;

    @Column(name = "SAVE_LOCATION", nullable = false)
    private String saveLocation;

    @Column(name = "SAVE_SIZE", nullable = false)
    private Long saveSize;

    @Column(name = "IS_USED")
    private boolean used = true;

    @Column(name = "CREATOR", nullable = false, length = 50)
    private String creator = SYSTEM_USER;   // 기본값

    @Column(name = "UPDATER", nullable = false, length = 50)
    private String updater = SYSTEM_USER;   // 기본값

    @Column(name = "FILE_TYPE", nullable = false)
    private String fileType;

    @Column(name = "FILE_ORDER")
    private Integer fileOrder;

    @Column(name = "IS_THUMBNAIL")
    private boolean thumbnail;


    /** 새로 업로드된 파일 정보로 파일 관련 속성을 한 번에 교체 */
    public void replaceWith(FileUploadInfo info) {
        this.fileName     = info.originalFileName();
        this.saveFileName = info.newFileName();
        this.saveLocation = info.saveLocation();
        this.saveSize     = info.fileSize();
        this.fileType     = info.fileType();
        // fileOrder · thumbnail · creator · updater 등은 유지
    }


    private static final String SYSTEM_USER = "SYSTEM";

    /* ---------- 생성자 ---------- */
    public SpaceFileInfo(Long spaceId,
                         String fileName,
                         String saveFileName,
                         String saveLocation,
                         Long saveSize,
                         String fileType,
                         Integer fileOrder,
                         boolean thumbnail) {
        this.spaceId      = spaceId;
        this.fileName     = fileName;
        this.saveFileName = saveFileName;
        this.saveLocation = saveLocation;
        this.saveSize     = saveSize;
        this.fileType     = fileType;
        this.fileOrder    = fileOrder;
        this.thumbnail    = thumbnail;
        // creator / updater 는 기본값(SYSTEM_USER) 유지
    }


    /* ---------- JPA Life-cycle ---------- */
    @PrePersist
    private void prePersist() {
        if (creator == null) creator = SYSTEM_USER;
        if (updater == null) updater = creator;
    }

    /* ---------- 비즈니스 로직 ---------- */
    public void updateCreator(String creator) {
        this.creator = creator;
    }

    public void updateUpdater(String updater) {
        this.updater = updater;
    }

    public void updateFileOrder(int fileOrder) {
        this.fileOrder = fileOrder;
    }

    /** 썸네일 여부 갱신용 메서드 */
    public void updateThumbnail(boolean thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void updateUsed(boolean used) {
        this.used = used;
    }

}
