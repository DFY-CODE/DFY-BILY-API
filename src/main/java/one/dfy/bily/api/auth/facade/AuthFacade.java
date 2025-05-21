package one.dfy.bily.api.auth.facade;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.auth.dto.*;
import one.dfy.bily.api.auth.model.PasswordResetToken;
import one.dfy.bily.api.auth.service.AuthService;
import one.dfy.bily.api.common.annotation.Facade;
import one.dfy.bily.api.terms.service.TermsService;
import one.dfy.bily.api.user.constant.SignInStatus;
import one.dfy.bily.api.user.dto.UserCommonResponse;
import one.dfy.bily.api.user.model.User;
import one.dfy.bily.api.user.service.UserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Facade
@RequiredArgsConstructor
public class AuthFacade {
    private final UserService userService;
    private final AuthService authService;
    private final TermsService termsService;

    public AuthCommonResponse sendSignUpEmailVerification(SendEmail request){
        if(userService.existUserByEmail(request.email())){
            return new AuthCommonResponse(false, "중복된 이메일입니다.");
        }
        return authService.sendSignUpEmailVerification(request);
    }

    public AuthCommonResponse checkPhoneNumber(String phoneNumber) {
        if(userService.checkPhoneNumber(phoneNumber)){
            return new AuthCommonResponse(false, "이미 가입한 휴대번호 입니다.");
        }
        return new AuthCommonResponse(true, "인증에 성공했습니다.");
    }

    @Transactional(rollbackFor = Exception.class)
    public JwtResponse signUp(SignUpRequest signUpRequest, MultipartFile businessCard, String clientIp, HttpServletResponse response) {
        authService.updateEmailUserId(signUpRequest.email(), signUpRequest.code());

        User user = userService.createUser(signUpRequest);

        authService.createBusinessCard(businessCard, user.getId());

        termsService.createUserTermAgreement(signUpRequest.termsCodeList(), user.getId());

        userService.createLoginHistory(user.getId(), clientIp, SignInStatus.SUCCESS);

        return authService.createRefreshToken(user, response);
    }

    public JwtResponse signIn(SignInRequest request, String clientIp, HttpServletResponse response) {
        User user = userService.signIn(request.email(), request.password(), clientIp);

        return authService.createRefreshToken(user, response);
    }

    public AuthCommonResponse sendPasswordResetVerification(SendEmail request){
        if(!userService.existUserByEmail(request.email())){
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }
        return authService.sendPasswordResetVerification(request);
    }


    @Transactional
    public UserCommonResponse resetPassword(PasswordResetRequest passwordResetRequest) {
        PasswordResetToken passwordResetToken = authService.checkPasswordResetToken(passwordResetRequest.token());
        return userService.updateUserPassword(passwordResetToken.getEmail(), passwordResetRequest.password());
    }

    @Transactional
    public CheckSignIn checkSignIn(Long userId) {
        userService.findUserById(userId);
        return new CheckSignIn(true);
    }

}
