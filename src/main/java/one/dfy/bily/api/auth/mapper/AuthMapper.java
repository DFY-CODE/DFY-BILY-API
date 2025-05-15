package one.dfy.bily.api.auth.mapper;

import one.dfy.bily.api.auth.model.AuthToken;
import one.dfy.bily.api.auth.model.BusinessCard;
import one.dfy.bily.api.common.dto.FileUploadInfo;
import one.dfy.bily.api.user.model.User;

import java.time.LocalDateTime;

public class AuthMapper {

    public static BusinessCard toBusinessCard(FileUploadInfo file, Long userId) {
        return new BusinessCard(
                userId,
                file.originalFileName(),
                file.newFileName(),
                file.saveLocation(),
                file.fileSize(),
                file.fileType()
        );
    }

    public static AuthToken toAuthToken(User user, String refreshToken, LocalDateTime expiresAt) {
        return new AuthToken(
                user,
                refreshToken,
                expiresAt
        );
    }

}
