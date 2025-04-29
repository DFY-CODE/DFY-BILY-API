package one.dfy.bily.api.admin.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.stream.Stream;

public enum InquiryStatus {
    NEW("신규"),
    IN_REVIEW("검토중"),
    DONE("대응완료");

    private final String description; // 한글 이름

    InquiryStatus(String description) {
        this.description = description;
    }

    @JsonValue
    public String getLabel() {
        return description;
    }

    @JsonCreator
    public static InquiryStatus fromLabel(String label) {
        return Stream.of(InquiryStatus.values())
                .filter(e -> e.description.equals(label))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown label: " + label));
    }
}