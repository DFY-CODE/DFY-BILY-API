package one.dfy.bily.api.common.mapper;

import one.dfy.bily.api.common.dto.FileInfoEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FileMapper {

    // 공간 이미지 파일 업로드
    @Insert("INSERT INTO bliydatabase.TBL_SPACE_FILE_INFO (CONTENT_ID, FILE_NAME, SAVE_FILE_NAME, SAVE_LOCATION, SAVE_SIZE, CREATE_DATE, CREATOR, DELETE_FLAG, UPDATER, FILE_TYPE) VALUES (#{contentId}, #{fileName}, #{saveFileName}, #{saveLocation}, #{saveSize}, #{createDate}, #{creator}, #{deleteFlag}, #{updater}, #{fileType})")
    void insertFile(FileInfoEntity fileInfoEntity);


    // 회원 명함 이미지 업로드
    @Insert("INSERT INTO TBL_FILE_BUSINESSCARD (USER_ID, FILE_NAME, SAVE_FILE_NAME, SAVE_LOCATION, SAVE_SIZE, CREATE_DATE, CREATOR, DELETE_FLAG, UPDATER, FILE_TYPE) VALUES (#{userId}, #{fileName}, #{saveFileName}, #{saveLocation}, #{saveSize}, #{createDate}, #{creator}, #{deleteFlag}, #{updater}, #{fileType})")
    void insertBusinessCardFile(FileInfoEntity fileInfoEntity);

    @Select(
            "SELECT " +
                    "  attach_file_id as attachFileId, " + // attach_file_id を attachFileIdに変更h, " +
                    "  content_id as contentId, " +
                    "  file_name as fileName, " +
                    "  save_file_name as saveFileName, " +
                    "  CONCAT('https://s3.ap-northeast-2.amazonaws.com/dfz.co.kr/', save_location) as saveLocation, " +
                    "  save_size as saveSize, " +
                    "  delete_flag as deleteFlag, " +
                    "  creator, " +
                    "  create_date as createDate, " +
                    "  updater, " +
                    "  update_date as updateDate, " +
                    "  file_type as fileType " +
                    "FROM " +
                    "  bliydatabase.TBL_SPACE_FILE_INFO " +
                    "WHERE " +
                    "  CONTENT_ID = #{contentId}"
    )
    List<FileInfoEntity> selectAllFiles(Long contentId);

    @Select("SELECT * FROM bliydatabase.TBL_SPACE_FILE_INFO WHERE ATTACH_FILE_ID = #{attachFileId}")
    FileInfoEntity selectFileById(Long attachFileId);

    @Delete("DELETE FROM bliydatabase.TBL_SPACE_FILE_INFO WHERE ATTACH_FILE_ID = #{attachFileId}")
    void deleteFile(Long attachFileId);

    @Update("UPDATE bliydatabase.TBL_SPACE_FILE_INFO SET FILE_NAME = #{fileName}, SAVE_FILE_NAME = #{saveFileName}, SAVE_LOCATION = #{saveLocation}, SAVE_SIZE = #{saveSize}, CREATE_DATE = #{createDate}, CREATOR = #{creator} WHERE ATTACH_FILE_ID = #{attachFileId}")
    void updateFile(FileInfoEntity fileInfoEntity);
}
