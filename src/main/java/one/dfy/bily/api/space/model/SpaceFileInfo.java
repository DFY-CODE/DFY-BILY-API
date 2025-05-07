package one.dfy.bily.api.space.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "TBL_SPACE_FILE_INFO")
public class SpaceFileInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ATTACH_FILE_ID")
    private Long id;

    @Column(name = "CONTENT_ID", nullable = false)
    private Integer contentId;

    @Column(name = "FILE_NAME", nullable = false)
    private String fileName;

    @Column(name = "SAVE_FILE_NAME")
    private String saveFileName;

    @Column(name = "SAVE_LOCATION", nullable = false)
    private String saveLocation;

    @Column(name = "SAVE_SIZE", nullable = false)
    private Long saveSize;

    @Column(name = "DELETE_FLAG")
    private String deleteFlag = "N";

    @Column(name = "CREATOR", nullable = false)
    private String creator;

    @Column(name = "CREATE_DATE", nullable = false)
    private LocalDateTime createDate;

    @Column(name = "UPDATER")
    private String updater;

    @Column(name = "UPDATE_DATE")
    private LocalDateTime updateDate;

    @Column(name = "FILE_TYPE", nullable = false)
    private String fileType;

    @Column(name = "FILE_ORDER")
    private Integer fileOrder;

    @Column(name = "IS_REPRESENTATIVE")
    private String isRepresentative = "N";
}