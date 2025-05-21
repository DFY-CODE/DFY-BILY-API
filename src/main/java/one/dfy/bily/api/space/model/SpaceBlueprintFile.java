package one.dfy.bily.api.space.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.dfy.bily.api.common.model.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "TBL_SPACE_BLUEPRINT_FILE_INFO")
public class SpaceBlueprintFile extends BaseEntity {

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
    private Boolean used = true;

    @Column(name = "CREATOR", nullable = false, length = 50)
    private String creator;

    @Column(name = "UPDATER", length = 50)
    private String updater;

    @Column(name = "FILE_TYPE", nullable = false, length = 50)
    private String fileType;

    public SpaceBlueprintFile(Long spaceId, String fileName, String saveFileName, String saveLocation, Long saveSize, String fileType) {
        this.spaceId = spaceId;
        this.fileName = fileName;
        this.saveFileName = saveFileName;
        this.saveLocation = saveLocation;
        this.saveSize = saveSize;
        this.fileType = fileType;
    }
}