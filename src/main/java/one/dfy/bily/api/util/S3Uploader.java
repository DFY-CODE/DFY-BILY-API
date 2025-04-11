package one.dfy.bily.api.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.dfy.bily.api.common.dto.*;
import one.dfy.bily.api.common.service.FileService;
import one.dfy.bily.api.common.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;
    private final FileService fileService;
    private final UserService userService;
    private static final ThreadLocal<String> usernameThreadLocal = new ThreadLocal<>();

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    public FileUploadResponse upload(long contentId, long filesize, String fileName, MultipartFile multipartFile, String dirName, String title) throws IOException {
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File로 전환이 실패했습니다."));

        FileInfoEntity fileInfoEntity = new FileInfoEntity();

        fileInfoEntity.setFileName(multipartFile.getOriginalFilename());
        String fileExtension = getFileExtension(fileName);
        String newFileName = "images/space/" + dirName + UUID.randomUUID().toString() + fileExtension;
        String saveFileName = dirName + UUID.randomUUID().toString() + fileExtension;

        fileInfoEntity.setContentId(contentId);
        fileInfoEntity.setSaveFileName(saveFileName);
        fileInfoEntity.setSaveLocation(newFileName);
        fileInfoEntity.setSaveSize(filesize);
        fileInfoEntity.setCreateDate(new Date());
        fileInfoEntity.setFileType(getFileExtension(fileName)); // 추가
        fileInfoEntity.setDeleteFlag("N");

// 사용자 정보를 가져와서 설정
        String username = usernameThreadLocal.get();
        if(username != null) {
            fileInfoEntity.setCreator(username);
            fileInfoEntity.setUpdater(username);
        } else {
            fileInfoEntity.setCreator("admin");
            fileInfoEntity.setUpdater("admin");
        }

// 파일 업로드 코드
        String fileUrl = putS3(uploadFile, newFileName);

// 파일 정보를 데이터베이스에 저장
        fileService.saveFile(fileInfoEntity);

        return new FileUploadResponse(fileUrl, fileInfoEntity.getFileName());
    }

    public List<SpaceFileDto> uploadFiles(
            Long contentId,
            List<MultipartFile> files,
            List<String> titles,
            List<String> orders,
            String fileType // "space" or "useCase"
    ) throws IOException {
        List<SpaceFileDto> fileDtos = new ArrayList<>();

        if (files != null && !files.isEmpty()) {
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);

                // 파일 유효성 검사
                if (file == null || file.getSize() <= 0) {
                    throw new IllegalArgumentException(
                            String.format(
                                    "파일 %d의 크기가 유효하지 않습니다. 크기: %d bytes",
                                    i,
                                    file != null ? file.getSize() : 0
                            )
                    );
                }

                // 파일 제목 설정
                String title = (titles != null && i < titles.size()) ? titles.get(i) : file.getOriginalFilename();

                // 파일 S3 업로드
                SpaceFileDto uploadedFile = spaceUpload(
                        contentId,
                        file.getSize(),
                        file.getOriginalFilename(),
                        file,
                        fileType,
                        title
                );

                // 파일 순서 설정
                int order = (orders != null && i < orders.size())
                        ? Integer.parseInt(orders.get(i))
                        : i + 1; // 기본 순서는 1부터 시작
                uploadedFile.setFileOrder(order);

                // 결과 리스트에 추가
                fileDtos.add(uploadedFile);
            }
        }

        return fileDtos;
    }


    // 공간정보 SpaceFileDto로 업로드
    public SpaceFileDto spaceUpload(long contentId, long filesize, String fileName, MultipartFile multipartFile, String dirName, String title) throws IOException {
        // 파일 변환 및 유효성 검사
        File uploadFile = convert(multipartFile).orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File로 변환 실패"));

        if (multipartFile.getSize() > 1024 * 1024 * 500) { // 500MB 확인
            throw new IllegalArgumentException("파일 크기는 500MB 이하로 업로드해야 합니다.");
        }

        // S3 저장용 파일 경로 및 이름 생성
        String fileExtension = getFileExtension(fileName);
        String newFileName = "images/space/space/" + dirName + UUID.randomUUID().toString() + fileExtension;

        // S3 업로드 및 URL 반환
        String fileUrl = putS3(uploadFile, newFileName);

        // SpaceFileDto 생성 및 반환
        SpaceFileDto spaceFileDto = new SpaceFileDto();
        spaceFileDto.setContentId(contentId);
        spaceFileDto.setFileOrder(0); // 순서는 컨트롤러에서 처리
        spaceFileDto.setFileName(multipartFile.getOriginalFilename());
        spaceFileDto.setSaveLocation(newFileName);
        spaceFileDto.setFileType(fileExtension);
        spaceFileDto.setFileSize(filesize);
        spaceFileDto.setTitle(title);

        return spaceFileDto;
    }


    // 공간이용 SpaceUseFileDto로 업로드
    public SpaceUseFileDto spaceUseUpload(long contentId, long filesize, String fileName, MultipartFile multipartFile, String dirName, String title) throws IOException {
        // 파일 변환 및 예외 처리
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File로 변환 실패"));

        // 파일 크기 제한 확인
        if (multipartFile.getSize() > 1024 * 1024 * 500) { // 500MB 확인
            throw new IllegalArgumentException("파일 크기는 500MB 이하로 업로드해야 합니다.");
        }

        // S3 저장 경로 및 파일 이름 생성
        String fileExtension = getFileExtension(fileName);
        String newFileName = "images/space/useCase/" + dirName + UUID.randomUUID() + fileExtension;

        // S3 업로드 및 URL 반환
        String fileUrl = putS3(uploadFile, newFileName);

        // SpaceUseFileDto 생성 및 반환
        SpaceUseFileDto spaceUseFileDto = new SpaceUseFileDto();
        spaceUseFileDto.setContentId(contentId);
        spaceUseFileDto.setFileOrder(0); // 순서는 컨트롤러에서 처리
        spaceUseFileDto.setFileName(multipartFile.getOriginalFilename());
        spaceUseFileDto.setSaveLocation(newFileName);
        spaceUseFileDto.setFileType(fileExtension);
        spaceUseFileDto.setFileSize(filesize);
        spaceUseFileDto.setTitle(title);

        return spaceUseFileDto;
    }

    // 공간이용 SpaceUseFileDto로 업로드
    public SpaceBulePrintFileDto blueprintUpload(long contentId, long filesize, String fileName, MultipartFile multipartFile, String dirName, String title) throws IOException {
        // 파일 변환 및 예외 처리
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File로 변환 실패"));

        // 파일 크기 제한 확인
        if (multipartFile.getSize() > 1024 * 1024 * 500) { // 500MB 확인
            throw new IllegalArgumentException("파일 크기는 500MB 이하로 업로드해야 합니다.");
        }

        // S3 저장 경로 및 파일 이름 생성
        String fileExtension = getFileExtension(fileName);
        String newFileName = "images/space/blueprint/" + dirName + UUID.randomUUID() + fileExtension;

        // S3 업로드 및 URL 반환
        putS3(uploadFile, newFileName);

        // SpaceUseFileDto 생성 및 반환
        SpaceBulePrintFileDto spaceBulePrintFileDto = new SpaceBulePrintFileDto();
        spaceBulePrintFileDto.setContentId(contentId);
        spaceBulePrintFileDto.setFileName(multipartFile.getOriginalFilename());
        spaceBulePrintFileDto.setSaveLocation(newFileName);
        spaceBulePrintFileDto.setFileType(fileExtension);
        spaceBulePrintFileDto.setFileSize(filesize);
        spaceBulePrintFileDto.setTitle(title);

        return spaceBulePrintFileDto;
    }


    public FileUploadResponse uploadBusinessCard(long userId, long filesize, String fileName, MultipartFile multipartFile, String dirName) throws IOException {
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File로 전환이 실패했습니다."));

        FileInfoEntity fileInfoEntity = new FileInfoEntity();

        fileInfoEntity.setFileName(multipartFile.getOriginalFilename());
        String fileExtension = getFileExtension(fileName);
        String newFileName = "images/business-card/" + dirName + UUID.randomUUID().toString() + fileExtension;
        String saveFileName = dirName + UUID.randomUUID().toString() + fileExtension;

        fileInfoEntity.setUserId(userId);
        fileInfoEntity.setSaveFileName(saveFileName);
        fileInfoEntity.setSaveLocation(newFileName);
        fileInfoEntity.setSaveSize(filesize);
        fileInfoEntity.setCreateDate(new Date());
        fileInfoEntity.setFileType(getFileExtension(fileName)); // 추가
        fileInfoEntity.setDeleteFlag("N");

// 사용자 정보를 가져와서 설정
        String username = usernameThreadLocal.get();
        if(username != null) {
            fileInfoEntity.setCreator(username);
            fileInfoEntity.setUpdater(username);
        } else {
            fileInfoEntity.setCreator("admin");
            fileInfoEntity.setUpdater("admin");
        }

// 파일 업로드 코드
        String fileUrl = putS3(uploadFile, newFileName);

// 파일 정보를 데이터베이스에 저장
        fileService.saveBusinessCardFile(fileInfoEntity);

        return new FileUploadResponse(fileUrl, fileInfoEntity.getFileName());
    }

    private String putS3(File uploadFile, String newFileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, newFileName, uploadFile));
        String url = "https://" + bucket + ".s3." + region + ".amazonaws.com/" + newFileName;
        return url;
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex > 0) {
            return fileName.substring(dotIndex);
        } else {
            return "";
        }
    }

    private String getTodayDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return simpleDateFormat.format(new Date());
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        } else {
            log.info("파일이 삭제되지 못했습니다.");
        }
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = File.createTempFile("temp", ".tmp");
        try (FileOutputStream fos = new FileOutputStream(convertFile)) {
            fos.write(file.getBytes());
        }
        return Optional.of(convertFile);
    }


}