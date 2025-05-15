package one.dfy.bily.api.user.constant;

public enum UserStatus {
    ACTIVE("사용"),
    INACTIVE("휴면"),
    DELETED("삭제")
    ;

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }
}
