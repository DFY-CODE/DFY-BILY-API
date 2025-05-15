package one.dfy.bily.api.user.mapper;

import one.dfy.bily.api.auth.dto.SignUpRequest;
import one.dfy.bily.api.user.constant.Role;
import one.dfy.bily.api.user.dto.Profile;
import one.dfy.bily.api.user.model.User;

public class UserMapper {
    public static User toUserEntity(SignUpRequest request, String encodePassword) {
        return new User(
                request.name(),
                encodePassword,
                request.email(),
                request.phoneNumber(),
                Role.USER
        );
    }

    public static Profile toUserInfo(User user) {
        return new Profile(
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber()
        );
    }
}
