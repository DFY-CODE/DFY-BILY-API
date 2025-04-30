package one.dfy.bily.api.space.mapper;

import one.dfy.bily.api.common.dto.*;
import one.dfy.bily.api.space.dto.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SpaceMapper {

    @Select("SELECT CONTENT_ID, DISPLAY_STATUS, SPACE_ID, PRICE, AREA_M2, NAME, AUTHOR, AMENITIES, AVAILABLE_USES, VIEWS  FROM TBL_SPACE LIMIT #{size} OFFSET #{offset}")
    @Results(id = "SpaceListDtoResultMap", value = {
            @Result(property = "contentId", column = "CONTENT_ID"),
            @Result(property = "displayStatus", column = "DISPLAY_STATUS"),
            @Result(property = "spaceId", column = "SPACE_ID"),
            @Result(property = "price", column = "PRICE"),
            @Result(property = "areaM2", column = "AREA_M2"),
            @Result(property = "name", column = "NAME"),
            @Result(property = "author", column = "AUTHOR"),
            @Result(property = "amenities", column = "AMENITIES"),
            @Result(property = "availableUses", column = "AVAILABLE_USES"),
            @Result(property = "views", column = "VIEWS")
    })
    List<SpaceListDto> getSpaces(@Param("size") int size, @Param("offset") int offset);

    // 여러 개의 ID를 기반으로 편의시설 조회
    @Select({
            "<script>",
            "SELECT AMENITY_ID, AMENITY_NAME FROM TBL_AMENITIES WHERE AMENITY_ID IN",
            "<foreach item='id' collection='amenityIds' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    List<AmenityDto> selectAmenitiesByIds(@Param("amenityIds") List<Integer> amenityIds);


    @Select("SELECT * FROM TBL_AMENITIES")
    List<AmenityDto> selectAmenitiesList();

    // 여러 개의 ID를 기반으로 이용 가능 용도 조회
    @Select({
            "<script>",
            "SELECT USE_ID, USE_NAME FROM TBL_AVAILABLE_USES WHERE USE_ID IN",
            "<foreach item='id' collection='useIds' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    List<AvailableUseDto> selectAvailableUsesByIds(@Param("useIds") List<Integer> useIds);

    @Select("SELECT * FROM TBL_AVAILABLE_USES")
    List<AvailableUseDto> selectAvailableUsesList();

    // 전체 공간 수 조회
    @Select("SELECT COUNT(*) FROM TBL_SPACE")
    int getTotalCount();


    @Select("SELECT CONTENT_ID, DISPLAY_STATUS, FIXED_STATUS, SPACE_ID, PRICE, AREA_M2, MAX_CAPACITY, DISTRICT_INFO, LOCATION, NAME, TAGS, INFO, FEATURES, FLOOR_PLAN, USAGE_TIME, CANCELLATION_POLICY, CREATED_AT, AUTHOR, AMENITIES, AVAILABLE_USES, AREA_PY, LATITUDE, LONGITUDE, VIEWS FROM TBL_SPACE WHERE CONTENT_ID = #{contentId}")
    SpaceDetailDto findSpaceDetailById(int contentId);

    @Select("SELECT ATTACH_FILE_ID as attachFileId, CONTENT_ID as contentId, FILE_NAME as fileName, SAVE_FILE_NAME as saveFileName, SAVE_LOCATION as saveLocation, SAVE_SIZE as saveSize, DELETE_FLAG as deleteFlag, CREATOR as creator, CREATE_DATE as createDate, UPDATER as updater, UPDATE_DATE as updateDate, FILE_TYPE as fileType, FILE_ORDER as fileOrder, IS_REPRESENTATIVE as isRepresentative FROM TBL_SPACE_FILE_INFO WHERE CONTENT_ID = #{contentId} AND DELETE_FLAG = 'N'")
    List<SpaceFileInfoDto> findSpaceFileInfoByContentId(int contentId);

    @Select("SELECT ATTACH_FILE_ID as attachFileId, CONTENT_ID as contentId, FILE_NAME as fileName, SAVE_FILE_NAME as saveFileName, SAVE_LOCATION as saveLocation, SAVE_SIZE as saveSize, DELETE_FLAG as deleteFlag, CREATOR as creator, CREATE_DATE as createDate, UPDATER as updater, UPDATE_DATE as updateDate, FILE_TYPE as fileType, FILE_ORDER as fileOrder, IS_REPRESENTATIVE as isRepresentative FROM TBL_SPACE_USE_FILE_INFO WHERE CONTENT_ID = #{contentId} AND DELETE_FLAG = 'N'")
    List<SpaceUseFileInfoDto> findSpaceUseFileInfoByContentId(int contentId);

    @Select("SELECT COALESCE(MAX(content_id), 0) FROM TBL_SPACE")
    Long getMaxContentId();

    @Insert("""
    INSERT INTO TBL_SPACE(
        CONTENT_ID, 
        SPACE_ID, 
        PRICE, 
        AREA_M2, 
        MAX_CAPACITY, 
        LOCATION, 
        TAGS, 
        INFO, 
        FEATURES, 
        USAGE_TIME, 
        CANCELLATION_POLICY, 
        AMENITIES, 
        AVAILABLE_USES, 
        AREA_PY, 
        LATITUDE, 
        LONGITUDE
    ) VALUES (
        #{contentId}, 
        #{spaceId}, 
        #{price}, 
        #{areaM2}, 
        #{maxCapacity}, 
        #{location}, 
        #{tags}, 
        #{info}, 
        #{features}, 
        #{usageTime}, 
        #{cancellationPolicy}, 
        #{amenities}, 
        #{availableUses}, 
        #{areaPy}, 
        #{latitude}, 
        #{longitude}
    )
""")
    void insertSpace(AdminSpaceDto spaceDto);




    @Update("""
    UPDATE TBL_SPACE SET 
        SPACE_ID = #{spaceId}, 
        PRICE = #{price}, 
        AREA_M2 = #{areaM2}, 
        MAX_CAPACITY = #{maxCapacity}, 
        LOCATION = #{location}, 
        TAGS = #{tags}, 
        INFO = #{info}, 
        FEATURES = #{features}, 
        USAGE_TIME = #{usageTime}, 
        CANCELLATION_POLICY = #{cancellationPolicy}, 
        AMENITIES = #{amenities}, 
        AVAILABLE_USES = #{availableUses}, 
        LATITUDE = #{latitude}, 
        LONGITUDE = #{longitude}, 
        AREA_PY = #{areaPy}, 
        UPDATE_DT = NOW() 
    WHERE CONTENT_ID = #{contentId}
""")
    void updateSpace(AdminSpaceDto spaceDto);


    @Insert("INSERT INTO TBL_SPACE_FILE_INFO (CONTENT_ID, FILE_NAME, SAVE_FILE_NAME, SAVE_LOCATION, SAVE_SIZE, CREATOR, CREATE_DATE, FILE_TYPE, FILE_ORDER, IS_REPRESENTATIVE) VALUES (#{contentId}, #{fileName}, #{saveFileName}, #{saveLocation}, #{fileSize}, #{creator}, NOW(), #{fileType}, #{fileOrder}, #{isRepresentative})")
    void insertSpaceFile(SpaceFileDto spaceFileInfoDto);

    @Update("UPDATE TBL_SPACE_FILE_INFO SET FILE_NAME = #{fileName}, SAVE_FILE_NAME = #{fileName}, SAVE_LOCATION = #{saveLocation}, SAVE_SIZE = #{fileSize}, UPDATER = #{updater}, UPDATE_DATE = NOW(), FILE_TYPE = #{fileType}, FILE_ORDER = #{fileOrder}, IS_REPRESENTATIVE = #{isRepresentative} WHERE ATTACH_FILE_ID = #{attachFileId}")
    void updateSpaceFile(SpaceFileDto spaceFileInfoDto);

    @Insert("INSERT INTO TBL_SPACE_USE_FILE_INFO (CONTENT_ID, FILE_NAME, SAVE_FILE_NAME, SAVE_LOCATION, SAVE_SIZE, CREATOR, CREATE_DATE, FILE_TYPE, FILE_ORDER, IS_REPRESENTATIVE) VALUES (#{contentId}, #{fileName}, #{fileName}, #{saveLocation}, #{fileSize}, #{creator}, NOW(), #{fileType}, #{fileOrder}, #{isRepresentative})")
    void insertSpaceUseFile(SpaceUseFileDto spaceUseFileInfoDto);

    @Update("UPDATE TBL_SPACE_USE_FILE_INFO SET FILE_NAME = #{fileName}, SAVE_FILE_NAME = #{saveFileName}, SAVE_LOCATION = #{saveLocation}, SAVE_SIZE = #{fileSize}, UPDATER = #{updater}, UPDATE_DATE = NOW(), FILE_TYPE = #{fileType}, FILE_ORDER = #{fileOrder}, IS_REPRESENTATIVE = #{isRepresentative} WHERE ATTACH_FILE_ID = #{attachFileId}")
    void updateSpaceUseFile(SpaceUseFileDto spaceUseFileInfoDto);

    @Update("UPDATE TBL_SPACE_FILE_INFO " +
            "SET FILE_ORDER = #{order} " +
            "WHERE ATTACH_FILE_ID = #{fileId}")
    void updateFileOrder(@Param("fileId") Long fileId, @Param("order") Integer order);

    @Update("UPDATE TBL_SPACE_USE_FILE_INFO " +
            "SET FILE_ORDER = #{order} " +
            "WHERE ATTACH_FILE_ID = #{fileId}")
    void updateUseFileOrder(@Param("fileId") Long fileId, @Param("order") Integer order);


    // 도면 정보 삽입
    @Insert("""
        INSERT INTO TBL_SPACE_BLUEPRINT_FILE_INFO (
            CONTENT_ID, FILE_NAME, SAVE_FILE_NAME, SAVE_LOCATION, SAVE_SIZE, DELETE_FLAG,
            CREATOR, CREATE_DATE, FILE_TYPE
        )
        VALUES (
            #{contentId}, #{fileName}, #{saveFileName}, #{saveLocation}, #{saveSize}, #{deleteFlag},
            #{creator}, NOW(), #{fileType}
        )
    """)
    void insertSpaceBulePrintFile(SpaceBulePrintFileDto spaceBulePrintFileDto);

    @Update("""
        UPDATE TBL_SPACE_BLUEPRINT_FILE_INFO
        SET 
            FILE_NAME = #{fileName},
            SAVE_FILE_NAME = #{saveFileName},
            SAVE_LOCATION = #{saveLocation},
            SAVE_SIZE = #{saveSize},
            DELETE_FLAG = #{deleteFlag},
            UPDATER = #{updater},
            UPDATE_DATE = NOW(),
            FILE_TYPE = #{fileType}
        WHERE ATTACH_FILE_ID = #{attachFileId}
    """)
    void updateSpaceBulePrintFile(SpaceBulePrintFileDto spaceBulePrintFileDto);

}


