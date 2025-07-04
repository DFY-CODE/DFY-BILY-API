package one.dfy.bily.api.space.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SpaceFileInfoDto {
    private Long attachFileId;
    private Long contentId;
    private String fileName;
    private String saveFileName;
    private String saveLocation;
    private Long saveSize;
    private String deleteFlag;
    private String creator;
    private LocalDateTime createDate;
    private String updater;
    private LocalDateTime updateDate;
    private String fileType;
    private Integer fileOrder;
    private String isRepresentative;
}
