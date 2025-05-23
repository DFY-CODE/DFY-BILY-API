package one.dfy.bily.api.inquiry.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum InquiryStatus {
    NEW("신규"),
    IN_REVIEW("검토중"),
    DONE("대응완료");

    private final String description; // 한글 이름

    InquiryStatus(String description) {
        this.description = description;
    }

    /**
     * JSON 변환 시 한글 이름(description)을 사용
     */
    @JsonValue
    public String getDescription() {
        return description;
    }

    /**
     * JSON 역직렬화 시 한글 이름(description)으로 Enum 값 변환
     */
    @JsonCreator
    public static InquiryStatus fromDescription(String description) {
        return Arrays.stream(InquiryStatus.values())
                .filter(status -> status.getDescription().equals(description))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown InquiryStatus description: " + description));
    }
}
