package one.dfy.bily.api.common.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Setter
@Getter
public class FileInfoEntity {
    /**
     * 첨부파일 아이디
     */
    private Long attachFileId;

    /**
     * 콘텐츠 아이디
     */
    private Long contentId;

    /**
     * 사용자 아이디
     */
    private Long userId;
    /**
     * 파일명
     */
    private String fileName;

    /**
     * 저장파일명
     */
    private String saveFileName;

    /**
     * 저장위치
     */
    private String saveLocation;

    /**
     * 저장크기
     */
    private Long saveSize;

    /**
     * 삭제구분
     */
    private String deleteFlag;

    /**
     * 생성자
     */
    private String creator;

    /**
     * 생성날짜
     */
    private Date createDate;

    /**
     * 수정자
     */
    private String updater;

    /**
     * 수정날짜
     */
    private Date updateDate;

    /**
     * 파일타입
     */
    private String fileType;

    // Getter, Setter 메서드
}
