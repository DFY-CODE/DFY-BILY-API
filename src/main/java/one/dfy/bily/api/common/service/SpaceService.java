package one.dfy.bily.api.common.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import one.dfy.bily.api.common.dto.*;
import one.dfy.bily.api.common.mapper.SpaceMapper;
import one.dfy.bily.api.util.S3Uploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpaceService {

    @Autowired
    private SpaceMapper spaceMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private S3Uploader s3Uploader;

    // 페이징 처리된 데이터 반환
    public List<AdminSpaceListDto> getSpaces(int page, int size) {
        int offset = (page - 1) * size;
        List<AdminSpaceListDto> spaces = spaceMapper.getSpaces(size, offset);

        for (AdminSpaceListDto space : spaces) {
            // JSON 문자열을 List<Integer>로 변환
            List<Integer> amenityIds = parseJsonArray(space.getAmenities());

            // amenities 리스트 설정
            List<AmenityDto> amenities = amenityIds.isEmpty() ? new ArrayList<>() : spaceMapper.selectAmenitiesByIds(amenityIds);
            space.setAmenitiesList(amenities);

            // availableUses 변환 및 설정
            List<Integer> useIds = parseJsonArray(space.getAvailableUses());
            List<AvailableUseDto> availableUses = useIds.isEmpty() ? new ArrayList<>() : spaceMapper.selectAvailableUsesByIds(useIds);
            space.setAvailableUsesList(availableUses);
        }


        return spaces;
    }

    public List<SpaceDetailDto> getSpacesDetail(int contentId) {
        List<SpaceDetailDto> spaces = new ArrayList<>();
        SpaceDetailDto space = spaceMapper.findSpaceDetailById(contentId);

        if (space != null) {
            // Space File Info
            space.setSpaceFileList(spaceMapper.findSpaceFileInfoByContentId(contentId));
            // Space Use File Info
            space.setSpaceUseFileList(spaceMapper.findSpaceUseFileInfoByContentId(contentId));

            // Tags, Features 문자열 파싱
            if (space.getTags() != null && !space.getTags().isEmpty()) {
                space.setTags(Arrays.stream(space.getTags().toString().replaceAll("^\\[|\\]$", "").split(","))
                        .map(String::trim).collect(Collectors.toList()));
            } else {
                space.setTags(new ArrayList<>());
            }

            if (space.getFeatures() != null && !space.getFeatures().isEmpty()) {
                space.setFeatures(Arrays.stream(space.getFeatures().toString().replaceAll("^\\[|\\]$", "").split(","))
                        .map(String::trim).collect(Collectors.toList()));
            } else {
                space.setFeatures(new ArrayList<>());
            }

            // 편의시설 List
            List<AmenityDto> amenities = spaceMapper.selectAmenitiesList();
            space.setAmenitiesList(amenities);

            // 이용 가능 용도
            List<AvailableUseDto> availableUses = spaceMapper.selectAvailableUsesList();
            space.setAvailableUsesList(availableUses);

            spaces.add(space);
        }

        return spaces;
    }




    // JSON 배열을 List<Integer>로 변환하는 메서드
    private List<Integer> parseJsonArray(String jsonArray) {
        try {
            if (jsonArray == null || jsonArray.isEmpty() || jsonArray.equals("[]")) {
                return new ArrayList<>();
            }

            // 1단계: JSON을 List<String>으로 변환
            List<String> stringList = objectMapper.readValue(jsonArray, new TypeReference<List<String>>() {});

            // 2단계: String 리스트를 Integer 리스트로 변환
            List<Integer> intList = new ArrayList<>();
            for (String s : stringList) {
                intList.add(Integer.parseInt(s));
            }
            return intList;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    // JSON 배열을 List<String>으로 변환하는 메서드
    private List<String> parseJsonStringArray(String jsonArray) {
        try {
            if (jsonArray == null || jsonArray.isEmpty() || jsonArray.equals("[]")) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(jsonArray, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // 전체 공간의 총 갯수
    public int getTotalCount() {
        return spaceMapper.getTotalCount();
    }

    public AdminSpaceDto buildAdminSpaceDto(Long contentId, SpaceDetailRequest dto) {
        AdminSpaceDto adminSpaceDto = new AdminSpaceDto();

        // 요청 데이터 매핑
        adminSpaceDto.setContentId(contentId);
        adminSpaceDto.setDisplayStatus(dto.getDisplayStatus());
        adminSpaceDto.setFixedStatus(dto.getFixedStatus());
        adminSpaceDto.setSpaceId(dto.getSpaceId());
        adminSpaceDto.setPrice(dto.getPrice() != null && dto.getPrice() > 0 ? dto.getPrice() : 0); // 기본값 검증
        adminSpaceDto.setAreaM2(dto.getAreaM2() != null ? dto.getAreaM2() : BigDecimal.ZERO);      // 기본값 검증
        adminSpaceDto.setMaxCapacity(dto.getMaxCapacity() != null ? dto.getMaxCapacity() : 1);     // 기본값 검증
        adminSpaceDto.setDistrictInfo(dto.getDistrictInfo());
        adminSpaceDto.setLocation(dto.getLocation());
        adminSpaceDto.setName(dto.getName());
        adminSpaceDto.setTags(dto.getTags()); // JSON 변환 예시
        adminSpaceDto.setInfo(dto.getInfo());
        adminSpaceDto.setFeatures(dto.getFeatures());
        adminSpaceDto.setUsageTime(dto.getUsageTime());
        adminSpaceDto.setCancellationPolicy(dto.getCancellationPolicy());

        return adminSpaceDto;
    }

    private static String convertToJson(String tags) {
        if (tags == null || tags.isEmpty()) {
            return "[]"; // 기본 빈 JSON 배열
        }
        // String을 파싱하여 JSON으로 변환 (Jackson 사용 예시)
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(tags.split(",")); // 쉼표로 분리된 태그를 배열로 변환
        } catch (JsonProcessingException e) {
            // 로깅 및 기본값 반환
            return "[]";
        }
    }

    // 공간 이용 사례 이미지 order 정보
    public  void updateUseFileOrder(Long fileId, Integer order){
        spaceMapper.updateUseFileOrder(fileId, order);
    }

    // 공간 이미지 order 정보
    public  void updateFileOrder(Long fileId, Integer order){
        spaceMapper.updateFileOrder(fileId, order);
    }

    public Long getNextContentId() {
        return spaceMapper.getMaxContentId() + 1;
    }

    public void insertSpace(AdminSpaceDto spaceDto,
                            List<SpaceFileDto> spaceFileDtos,
                            List<SpaceUseFileDto> spaceUseFileDtos,
                            List<SpaceBulePrintFileDto> spaceBulePrintFileDtos) {
        // 공간 정보 삽입
        spaceMapper.insertSpace(spaceDto);

        // 공간 파일 정보 삽입
        if (spaceFileDtos != null && !spaceFileDtos.isEmpty()) {
            spaceFileDtos.forEach(spaceFileDto -> {
                spaceFileDto.setContentId(spaceDto.getContentId());
                spaceMapper.insertSpaceFile(spaceFileDto);
            });
        }

        // 공간 이용 파일 정보 삽입
        if (spaceUseFileDtos != null && !spaceUseFileDtos.isEmpty()) {
            spaceUseFileDtos.forEach(spaceUseFileDto -> {
                spaceUseFileDto.setContentId(spaceDto.getContentId());
                spaceMapper.insertSpaceUseFile(spaceUseFileDto);
            });
        }

        // 도면 파일 정보 삽입
        if (spaceBulePrintFileDtos != null && !spaceBulePrintFileDtos.isEmpty()) {
            spaceBulePrintFileDtos.forEach(spaceBulePrintFileDto -> {
                spaceBulePrintFileDto.setContentId(spaceDto.getContentId());
                spaceMapper.insertSpaceBulePrintFile(spaceBulePrintFileDto);
            });
        }
    }



    // 공간 정보 수정
    public void updateSpace(AdminSpaceDto spaceDto, List<SpaceFileDto> spaceFileDtos, List<SpaceUseFileDto> spaceUseFileDtos, List<SpaceBulePrintFileDto> spaceBulePrintFileDtos) {
        // 공간 정보 업데이트
        spaceMapper.updateSpace(spaceDto);

        // 공간 파일 정보 업데이트 또는 삽입
        if (spaceFileDtos != null && !spaceFileDtos.isEmpty()) {
            spaceFileDtos.forEach(spaceFileDto -> {
                spaceFileDto.setContentId(spaceDto.getContentId());
                if (spaceFileDto.getAttachFileId() == null) {
                    spaceMapper.insertSpaceFile(spaceFileDto);
                } else {
                    spaceMapper.updateSpaceFile(spaceFileDto);
                }
            });
        }

        // 공간 이용 파일 정보 업데이트 또는 삽입
        if (spaceUseFileDtos != null && !spaceUseFileDtos.isEmpty()) {
            spaceUseFileDtos.forEach(spaceUseFileDto -> {
                spaceUseFileDto.setContentId(spaceDto.getContentId());
                if (spaceUseFileDto.getAttachFileId() == null) {
                    spaceMapper.insertSpaceUseFile(spaceUseFileDto);
                } else {
                    spaceMapper.updateSpaceUseFile(spaceUseFileDto);
                }
            });
        }

        // 도면 정보 업데이트 또는 삽입
        if (spaceBulePrintFileDtos != null && !spaceBulePrintFileDtos.isEmpty()) {
            spaceBulePrintFileDtos.forEach(spaceBulePrintFileDto -> {
                spaceBulePrintFileDto.setContentId(spaceDto.getContentId());
                if (spaceBulePrintFileDto.getAttachFileId() == null) {
                    spaceMapper.insertSpaceBulePrintFile(spaceBulePrintFileDto);
                } else {
                    spaceMapper.updateSpaceBulePrintFile(spaceBulePrintFileDto);
                }
            });
        }

    }





}
