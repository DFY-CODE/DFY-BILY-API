package one.dfy.bily.api.user.service;

import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.common.dto.AdminUser;
import one.dfy.bily.api.common.dto.User;
import one.dfy.bily.api.common.mapper.UserMapper;
import one.dfy.bily.api.user.model.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;


    public AdminUser findByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    public void registerUser(String userName, String rawPassword) {
        String encodedPassword = passwordEncoder.encode(rawPassword);
        AdminUser user = new AdminUser();
        user.setUserName(userName);
        user.setPassword(encodedPassword);
        userMapper.insertUser(user);
    }

    // 회원가입 기존 인증된 이메일
    public Long updatePassword(String email, String rawPassword, String username, String gender, String birthDate) {
        String encodedPassword = passwordEncoder.encode(rawPassword);
        return userMapper.updatePassword(email, encodedPassword, username, gender, birthDate); //변경된 Mapper의 결과를 반환합니다.
    }

    public Optional<User> getUserById(Long userId) {
        return userMapper.findUserById(userId);
    }

    public void updateUser(User user) {
        userMapper.updateUser(user);
    }

    public one.dfy.bily.api.user.model.User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("올바르지 않은 회원 정보입니다."));
    }


}

