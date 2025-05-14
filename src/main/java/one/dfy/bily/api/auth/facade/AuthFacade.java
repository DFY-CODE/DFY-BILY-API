package one.dfy.bily.api.auth.facade;

import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.auth.dto.AuthCommonResponse;
import one.dfy.bily.api.auth.dto.SignInRequest;
import one.dfy.bily.api.auth.dto.SignUpRequest;
import one.dfy.bily.api.auth.dto.TokenResponse;
import one.dfy.bily.api.auth.service.AuthService;
import one.dfy.bily.api.common.annotation.Facade;
import one.dfy.bily.api.terms.service.TermsService;
import one.dfy.bily.api.user.model.User;
import one.dfy.bily.api.user.service.UserService;

@Facade
@RequiredArgsConstructor
public class AuthFacade {
    private final UserService userService;
    private final AuthService authService;
    private final TermsService termsService;

    public AuthCommonResponse checkPhoneNumber(String phoneNumber) {
        if(userService.checkPhoneNumber(phoneNumber)){
            return new AuthCommonResponse(false, "이미 가입한 휴대번호 입니다.");
        }
        return new AuthCommonResponse(true, "인증에 성공했습니다.");
    }

    public TokenResponse signUp(SignUpRequest signUpRequest) {
        User user = userService.createUser(signUpRequest);

        authService.createBusinessCard(signUpRequest.businessCard(), user.getId());

        termsService.createUserTermAgreement(signUpRequest.termsCodeList(), user.getId());

        return authService.createRefreshToken(user);
    }

    public TokenResponse signIn(SignInRequest request) {
        User user = userService.findByEmail(request.email(), request.password());

        return authService.createRefreshToken(user);
    }

}
