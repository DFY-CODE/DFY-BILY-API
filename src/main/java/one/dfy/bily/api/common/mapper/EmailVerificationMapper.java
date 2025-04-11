package one.dfy.bily.api.common.mapper;

import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;

@Mapper
public interface EmailVerificationMapper {

    // 이메일과 인증 코드 확인 (유효한 코드인지 확인)
    @Select("SELECT COUNT(1) FROM TBL_USER " +
            "WHERE EMAIL = #{email} AND VERIFICATION_CODE = #{verificationCode} " +
            "AND EXPIRATION_AT > CURRENT_TIMESTAMP") // 만료 시간이 현재보다 뒤일 경우만 유효
    boolean verifyCode(@Param("email") String email, @Param("verificationCode") String verificationCode);

    // 인증 코드와 이메일을 DB에 저장
    @Insert("INSERT INTO TBL_USER (ACCESS_TOKEN, EXPIRATION_AT, EMAIL, VERIFICATION_CODE) " +
            "VALUES (#{token}, #{expirationTime}, #{email}, #{verificationCode})")
    void insertVerificationToken(@Param("email") String email,
                                 @Param("token") String token,
                                 @Param("verificationCode") String verificationCode,
                                 @Param("expirationTime") LocalDateTime expirationTime);
}


