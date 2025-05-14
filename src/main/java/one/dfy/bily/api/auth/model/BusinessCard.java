package one.dfy.bily.api.auth.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.dfy.bily.api.common.model.BaseEntity;

@Entity
@Table(name = "TBL_BUSINESS_CARD")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BusinessCard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USER_ID")
    private Long userId;

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

    @Column(name = "FILE_TYPE", nullable = false, length = 50)
    private String fileType;

    public BusinessCard(Long userId, String fileName, String saveFileName, String saveLocation, Long saveSize, String fileType) {
        this.userId = userId;
        this.fileName = fileName;
        this.saveFileName = saveFileName;
        this.saveLocation = saveLocation;
        this.saveSize = saveSize;
        this.fileType = fileType;
    }
}
