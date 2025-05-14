package one.dfy.bily.api.user.service;

import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.auth.dto.SignUpRequest;
import one.dfy.bily.api.user.mapper.UserMapper;
import one.dfy.bily.api.user.model.User;
import one.dfy.bily.api.user.model.repository.UserRepository;
import one.dfy.bily.api.util.JwtProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("올바르지 않은 회원 정보입니다."));
    }

    @Transactional(readOnly = true)
    public boolean checkPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }


    @Transactional
    public User createUser(SignUpRequest signUpRequest) {
        String encodePassword = passwordEncoder.encode(signUpRequest.password());

        return userRepository.save(UserMapper.toUserEntity(signUpRequest, encodePassword));
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email, String password) {
         User userEntity = userRepository.findByEmail(email)
                 .orElseThrow(() -> new IllegalArgumentException("올바르지 않은 회원 정보입니다."));

         if(!passwordEncoder.matches(password,userEntity.getPassword())) {
             throw new IllegalArgumentException("올바르지 않은 회원 정보입니다.");
         }

        return userEntity;
    }

}
