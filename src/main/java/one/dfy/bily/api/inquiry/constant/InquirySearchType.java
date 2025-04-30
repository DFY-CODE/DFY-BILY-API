package one.dfy.bily.api.inquiry.constant;

public enum InquirySearchType {
    SPACE("공간명"),
    COMPANY_NAME("회사명"),
    CONTACT_PERSON("이름")
    ;

    private final String description;

    InquirySearchType(String description) {
        this.description = description;
    }

    public static InquirySearchType fromDescription(String description) {
        for (InquirySearchType type : InquirySearchType.values()) {
            if (type.description.equals(description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown description: " + description);
    }

    public String getDescription() {
        return description;
    }
}
