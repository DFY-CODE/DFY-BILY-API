package one.dfy.bily.api.terms.constant;

import lombok.Getter;

@Getter
public enum TermsCode {
    TERMS_OF_SERVICE("서비스 이용약관 동의"),
    PRIVACY_POLICY("개인정보 수집 이용 동의"),
    AGE_OVER_14("만 14세 이상 확인");

    private final String description;

    TermsCode(String description) {
        this.description = description;
    }
}
