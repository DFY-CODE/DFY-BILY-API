package one.dfy.bily.api.space.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder // Builder 패턴 추가
@AllArgsConstructor // 모든 필드 포함 생성자 추가
@NoArgsConstructor  // 기본 생성자 추가
public class SpaceUseFileDto {
    private Long attachFileId;
    private Long contentId;
    private String fileName;
    private String saveFileName;
    private String saveLocation;
    private Long fileSize;
    private String title; // 추가 필드 예시
    @Builder.Default
    private String deleteFlag = "N"; // 기본값: N

    @Builder.Default
    private String creator = "system"; // 기본값: system

    @Builder.Default
    private String updater = "system"; // 기본값: system

    private String fileType;
    private Integer fileOrder;

    @Builder.Default
    private String isRepresentative = "N"; // 기본값: N

}


