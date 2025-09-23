package one.dfy.bily.api.space.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import one.dfy.bily.api.common.dto.FileUploadInfo;
import one.dfy.bily.api.common.model.BaseEntity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import one.dfy.bily.api.common.model.BaseEntity;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "TBL_SPACE_USE_FILE_INFO")
public class SpaceUseFileInfo extends BaseEntity {

    private static final String SYSTEM_USER = "SYSTEM";

    /* ---------- 컬럼 ---------- */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private Boolean used = true;

    @Column(name = "CREATOR", nullable = false, length = 50)
    private String creator = SYSTEM_USER;   // 기본값

    @Column(name = "UPDATER", nullable = false, length = 50)
    private String updater = SYSTEM_USER;   // 기본값

    @Column(name = "FILE_TYPE", nullable = false, length = 50)
    private String fileType;

    @Column(name = "FILE_ORDER")
    private Integer fileOrder;

    @Column(name = "FILE_TITLE")
    private String fileTitle;

    @Column(name = "IS_THUMBNAIL")
    private Boolean thumbnail;

    /* ---------- JPA 라이프사이클 ---------- */
    @jakarta.persistence.PrePersist
    private void onPersist() {
        if (isBlank(this.creator)) this.creator = SYSTEM_USER;
        if (isBlank(this.updater)) this.updater = SYSTEM_USER;
    }

    @jakarta.persistence.PreUpdate
    private void onUpdate() {
        if (isBlank(this.updater)) this.updater = SYSTEM_USER;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    /* ---------- 생성자 ---------- */
    // 기존 생성자가 있을 수 있으므로 유지. 필요 시 다음과 같은 명시적 생성자도 사용 가능합니다.
    public SpaceUseFileInfo(Long spaceId,
                            String fileName,
                            String saveFileName,
                            String saveLocation,
                            Long saveSize,
                            String fileType,
                            Integer fileOrder,
                            Boolean used,
                            String fileTitle,
                            Boolean thumbnail) {
        this.spaceId = spaceId;
        this.fileName = fileName;
        this.saveFileName = saveFileName;
        this.saveLocation = saveLocation;
        this.saveSize = saveSize;
        this.fileType = fileType;
        this.fileOrder = fileOrder;
        this.used = used;
        this.fileTitle = fileTitle;
        this.thumbnail = thumbnail != null ? thumbnail : Boolean.FALSE;
    }
}

