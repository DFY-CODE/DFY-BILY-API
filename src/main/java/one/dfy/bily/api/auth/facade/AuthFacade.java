package one.dfy.bily.api.auth.facade;

import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.auth.dto.AuthCommonResponse;
import one.dfy.bily.api.auth.dto.SignInRequest;
import one.dfy.bily.api.auth.dto.SignUpRequest;
import one.dfy.bily.api.auth.dto.TokenResponse;
import one.dfy.bily.api.auth.service.AuthService;
import one.dfy.bily.api.common.annotation.Facade;
import one.dfy.bily.api.terms.service.TermsService;
import one.dfy.bily.api.user.constant.LoginStatus;
import one.dfy.bily.api.user.model.User;
import one.dfy.bily.api.user.service.UserService;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(rollbackFor = Exception.class)
    public TokenResponse signUp(SignUpRequest signUpRequest, String clientIp) {
        User user = userService.createUser(signUpRequest);

        authService.createBusinessCard(signUpRequest.businessCard(), user.getId());

        termsService.createUserTermAgreement(signUpRequest.termsCodeList(), user.getId());

        userService.createLoginHistory(user.getId(), clientIp, LoginStatus.SUCCESS);

        return authService.createRefreshToken(user);
    }

    public TokenResponse signIn(SignInRequest request, String clientIp) {
        User user = userService.findByEmail(request.email(), request.password(), clientIp);

        return authService.createRefreshToken(user);
    }

}
