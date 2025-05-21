package one.dfy.bily.api.user.service;

import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.auth.dto.SignUpRequest;
import one.dfy.bily.api.common.model.BaseEntity;
import one.dfy.bily.api.inquiry.dto.InquiryPreferredDateInfo;
import one.dfy.bily.api.user.constant.SignInStatus;
import one.dfy.bily.api.user.model.BusinessCard;
import one.dfy.bily.api.user.model.SignInHistory;
import one.dfy.bily.api.user.model.repository.BusinessCardRepository;
import one.dfy.bily.api.common.dto.Pagination;
import one.dfy.bily.api.common.mapper.PaginationMapper;
import one.dfy.bily.api.user.constant.UserSearchDateType;
import one.dfy.bily.api.user.constant.UserStatus;
import one.dfy.bily.api.user.dto.*;
import one.dfy.bily.api.user.mapper.UserMapper;
import one.dfy.bily.api.user.model.User;
import one.dfy.bily.api.user.model.repository.SignInHistoryRepository;
import one.dfy.bily.api.user.model.repository.UserRepository;
import one.dfy.bily.api.util.EmailMaskingUtil;
import one.dfy.bily.api.util.S3Uploader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SignInHistoryRepository signInHistoryRepository;
    private final BusinessCardRepository businessCardRepository;
    private final S3Uploader s3Uploader;

    @Transactional(readOnly = true)
    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("올바르지 않은 회원 정보입니다."));
    }

    @Transactional(readOnly = true)
    public boolean checkPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumberAndStatus(phoneNumber, UserStatus.ACTIVE);
    }

    public User createUser(SignUpRequest signUpRequest) {
        String encodePassword = passwordEncoder.encode(signUpRequest.password());

        return userRepository.save(UserMapper.toUserEntity(signUpRequest, encodePassword));
    }

    @Transactional
    public User signIn(String email, String password, String clientIp) {
         User userEntity = userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
                 .orElseThrow(() -> new IllegalArgumentException("올바르지 않은 회원 정보입니다."));
        SignInHistory signInHistory = createLoginHistory(userEntity.getId(), clientIp, SignInStatus.FAIL);

         if(!passwordEncoder.matches(password,userEntity.getPassword())) {
             throw new IllegalArgumentException("올바르지 않은 회원 정보입니다.");
         }

        signInHistory.changeLoginStatus(SignInStatus.SUCCESS);

        return userEntity;
    }

    @Transactional(readOnly = true)
    public Profile findProfileById(Long id){
        User userEntity = findUserById(id);

        return UserMapper.toUserInfo(userEntity);
    }

    @Transactional
    public SignInHistory createLoginHistory(Long userId, String clientIp, SignInStatus signInStatus) {
        SignInHistory signInHistory = new SignInHistory(
                userId,
                signInStatus,
                clientIp
        );
        return signInHistoryRepository.save(signInHistory);
    }

    @Transactional(readOnly = true)
    public UserInfoList findUserInfoList(String email, UserSearchDateType userSearchDateType, LocalDateTime startAt, LocalDateTime endAt, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page-1, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        Page<UserInfo> userInfoPage = userRepository.findUsersWithRecentLogin(email, userSearchDateType, startAt, endAt, true, pageable);

        Pagination pagination = PaginationMapper.toPagination(pageable, userInfoPage.getTotalElements(), userInfoPage.getTotalPages());
        return new UserInfoList(userInfoPage.getContent(), pagination);
    }

    @Transactional(readOnly = true)
    public UserDetailInfo findUserDetailInfo(Long userId) {
        User userEntity = findUserById(userId);

        String businessCardUrl = businessCardRepository.findByUserIdAndUsed(userEntity.getId(), true)
                .map(card -> s3Uploader.getBusinessCardS3Url() + card.getSaveFileName())
                .orElse(null);

        LocalDateTime recentSignInDateTime = signInHistoryRepository.findTopByUserIdOrderByIdDesc(userEntity.getId())
                .map(BaseEntity::getCreatedAt)
                .orElse(null);

        return UserMapper.toUserDetailInfo(userEntity, businessCardUrl, recentSignInDateTime);
    }

    @Transactional
    public UserCommonResponse deleteUserById(Long userId) {
        User userEntity = findUserById(userId);
        userEntity.updateStatus(UserStatus.DELETED);
        return new UserCommonResponse(true, "회원 삭제가 완료되었습니다.");
    }

    @Transactional
    public UserCommonResponse updateUserPassword(Long userId, UpdatePassword password) {
        User userEntity = findUserById(userId);

        if(!passwordEncoder.matches(password.originalPassword(), userEntity.getPassword())) {
            throw new IllegalArgumentException("올바르지 않은 회원 정보입니다.");
        }

        String encodePassword = passwordEncoder.encode(password.newPassword());
        userEntity.updatePassword(encodePassword);

        return new UserCommonResponse(true, "비밀번호 변경이 완료되었습니다.");
    }

    @Transactional
    public UserCommonResponse updatePhoneNumber(Long userId, PhoneNumber request) {
        User userEntity = findUserById(userId);
        userEntity.updatePhoneNumber(request.phoneNumber());

        return new UserCommonResponse(true, "휴대폰 번호 변경이 완료되었습니다.");
    }

    @Transactional(readOnly = true)
    public MaskingUserEmail findMaskingUserEmail(String name, String phoneNumber) {
        User user = userRepository.findByNameAndPhoneNumberAndStatus(name, phoneNumber, UserStatus.ACTIVE)
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
    public boolean existUserByEmail(String email) {
         return userRepository.existsByEmailAndStatus(email, UserStatus.ACTIVE);
    }

    @Transactional
    public UserCommonResponse updateUserPassword(String email, String password) {
        User userEntity = findUserByEmail(email);
        String encodePassword = passwordEncoder.encode(password);
        userEntity.updatePassword(encodePassword);

        return new UserCommonResponse(true, "비밀번호 변경이 완료되었습니다.");
    }

    @Transactional(readOnly = true)
    public Map<Long, String> findByIds(List<Long> ids) {
        return userRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(
                        User::getId,
                        User::getName
                ));
    }

}
