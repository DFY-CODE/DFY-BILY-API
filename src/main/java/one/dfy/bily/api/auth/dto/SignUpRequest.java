package one.dfy.bily.api.auth.dto;

import one.dfy.bily.api.terms.constant.TermsCode;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record SignUpRequest(
        String name,
        String email,
        String password,
        String phoneNumber,
        List<TermsCode> termsCodeList
) {
}
