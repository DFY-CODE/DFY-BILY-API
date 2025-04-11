package one.dfy.bily.api.common.mapper;

import one.dfy.bily.api.common.dto.AdminUser;
import one.dfy.bily.api.common.dto.User;
import org.apache.ibatis.annotations.*;

import java.util.Optional;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM TBL_USER WHERE EMAIL = #{email}")
    AdminUser findByEmail(@Param("email") String email);

    @Insert("INSERT INTO TBL_USER (USER_NAME, PASSWORD, ACCESS_TOKEN, REFRESH_TOKEN, EXPIRATION_AT) VALUES (#{userName}, #{password}, #{accessToken}, #{refreshToken}, #{expirationAt}, #{email})")
    void insertUser(AdminUser user);

    @Update("UPDATE TBL_USER SET PASSWORD = #{password}, USER_NAME = #{username}, GENDER = #{gender}, BIRTH_DATE = #{birthDate} WHERE EMAIL = #{email}")
    Long updatePassword(@Param("email") String email, @Param("password") String password, @Param("username") String username, @Param("gender") String gender, @Param("birthDate") String birthDate);

    @Select("SELECT " +
            "CASE " +
            "    WHEN a.save_location IS NOT NULL THEN CONCAT('https://s3.ap-northeast-2.amazonaws.com/dfz.co.kr/', a.save_location) " +
            "    ELSE NULL " +
            "END AS saveLocation, " +
            "b.* " +
            "FROM " +
            "    TBL_USER b " +
            "LEFT JOIN " +
            "    TBL_FILE_BUSINESSCARD a ON b.USER_ID = a.USER_ID " +
            "WHERE " +
            "    b.USER_ID = #{userId}")
    Optional<User> findUserById(Long userId);

    @Update("UPDATE TBL_USER SET USER_NAME = #{userName}, PHONE_NUMBER = #{phoneNumber}, GENDER = #{gender}, BIRTH_DATE = #{birthDate} WHERE USER_ID = #{userId}")
    void updateUser(User user);
}


