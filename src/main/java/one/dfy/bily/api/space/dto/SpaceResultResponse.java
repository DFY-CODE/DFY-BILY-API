package one.dfy.bily.api.space.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SpaceResultResponse{

        private boolean success;
        private String message;
        private String spaceId;   // ★ 추가

        public static SpaceResultResponse ok(String spaceId) {
        return SpaceResultResponse.builder()
                .success(true)
                                  .message("저장되었습니다.")
                                  .spaceId(spaceId)
                                  .build();
        }
}
