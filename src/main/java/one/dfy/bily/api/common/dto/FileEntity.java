package one.dfy.bily.api.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class FileEntity {
    private Long id;
    private String originalFileName;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private Date uploadDate;
    private String username;

    // Getter, Setter 메서드
}
