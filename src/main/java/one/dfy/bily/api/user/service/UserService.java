package one.dfy.bily.api.user.service;

import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.auth.dto.SignUpRequest;
import one.dfy.bily.api.common.dto.Pagination;
import one.dfy.bily.api.common.mapper.PaginationMapper;
import one.dfy.bily.api.user.constant.LoginStatus;
import one.dfy.bily.api.user.constant.UserSearchDateType;
import one.dfy.bily.api.user.constant.UserStatus;
import one.dfy.bily.api.user.dto.*;
import one.dfy.bily.api.user.mapper.UserMapper;
import one.dfy.bily.api.user.model.LoginHistory;
import one.dfy.bily.api.user.model.User;
import one.dfy.bily.api.user.model.repository.LoginHistoryRepository;
import one.dfy.bily.api.user.model.repository.UserRepository;
import one.dfy.bily.api.util.EmailMaskingUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginHistoryRepository loginHistoryRepository;

    @Transactional(readOnly = true)
    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("올바르지 않은 회원 정보입니다."));
    }

    @Transactional(readOnly = true)
    public boolean checkPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    public User createUser(SignUpRequest signUpRequest) {
        String encodePassword = passwordEncoder.encode(signUpRequest.password());

        return userRepository.save(UserMapper.toUserEntity(signUpRequest, encodePassword));
    }

    @Transactional
    public User signIn(String email, String password, String clientIp) {
         User userEntity = userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
                 .orElseThrow(() -> new IllegalArgumentException("올바르지 않은 회원 정보입니다."));
        LoginHistory loginHistory = createLoginHistory(userEntity.getId(), clientIp, LoginStatus.FAIL);

         if(!passwordEncoder.matches(password,userEntity.getPassword())) {
             throw new IllegalArgumentException("올바르지 않은 회원 정보입니다.");
         }

        loginHistory.changeLoginStatus(LoginStatus.SUCCESS);

        return userEntity;
    }

    @Transactional(readOnly = true)
    public Profile findProfileById(Long id){
        User userEntity = findUserById(id);

        return UserMapper.toUserInfo(userEntity);
    }

    @Transactional
    public LoginHistory createLoginHistory(Long userId, String clientIp, LoginStatus loginStatus) {
        LoginHistory loginHistory = new LoginHistory(
                userId,
                loginStatus,
                clientIp
        );
        return loginHistoryRepository.save(loginHistory);
    }

    @Transactional(readOnly = true)
    public UserInfoList findUserInfoList(String email, UserSearchDateType userSearchDateType, LocalDate date, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page-1, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        Page<UserInfo> userInfoPage = userRepository.findUsersWithRecentLogin(email, userSearchDateType, date, true, pageable);

        Pagination pagination = PaginationMapper.toPagination(pageable, userInfoPage.getTotalElements(), userInfoPage.getTotalPages());
        return new UserInfoList(userInfoPage.getContent(), pagination);
    }

    @Transactional
    public UserCommonResponse deleteUserById(Long userId) {
        User userEntity = findUserById(userId);
        userEntity.updateStatus(UserStatus.DELETED);
        return new UserCommonResponse(true, "회원 삭제가 완료되었습니다.");
    }

    @Transactional
    public UserCommonResponse updateUserPassword(Long userId, String password) {
        User userEntity = findUserById(userId);
        String encodePassword = passwordEncoder.encode(password);
        userEntity.updatePassword(encodePassword);

        return new UserCommonResponse(true, "비밀번호 변경이 완료되었습니다.");
    }

    @Transactional
    public UserCommonResponse updatePhoneNumber(Long userId, String phoneNumber) {
        User userEntity = findUserById(userId);
        userEntity.updatePhoneNumber(phoneNumber);

        return new UserCommonResponse(true, "휴대폰 번호 변경이 완료되었습니다.");
    }

    @Transactional(readOnly = true)
    public MaskingUserEmail findMaskingUserEmail(String name, String phoneNumber) {
        User user = userRepository.findByNameAndPhoneNumber(name, phoneNumber)
                .orElseThrow(() -> new IllegalArgumentException("올바르지 않은 회원 정보입니다."));

        String maskingEmail = EmailMaskingUtil.maskEmail(user.getEmail());

        return new MaskingUserEmail(maskingEmail);
    }

    @Transactional(readOnly = true)
    public User findUserByEmail(String email) {
        return userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("이메일이 존재하지 않습니다."));
    }

    @Transactional
    public void existUserByEmail(String email) {
         if(!userRepository.existsByEmailAndStatus(email, UserStatus.ACTIVE)) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
         }
    }

    @Transactional
    public UserCommonResponse updateUserPassword(String email, String password) {
        User userEntity = findUserByEmail(email);
        String encodePassword = passwordEncoder.encode(password);
        userEntity.updatePassword(encodePassword);

        return new UserCommonResponse(true, "비밀번호 변경이 완료되었습니다.");
    }

}
