package one.dfy.bily.api.common.dto;

import lombok.Data;
import java.util.Date;

@Data
public class BusinessCard {
    private Long attachFileId;
    private Long userId;
    private String fileName;
    private String saveFileName;
    private String saveLocation;
    private Long saveSize;
    private String deleteFlag;
    private String creator;
    private Date createDate;
    private String updater;
    private Date updateDate;
    private String fileType;
}
