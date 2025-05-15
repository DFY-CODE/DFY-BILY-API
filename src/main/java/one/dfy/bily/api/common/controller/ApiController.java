package one.dfy.bily.api.common.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import one.dfy.bily.api.common.dto.FileInfoEntity;
import one.dfy.bily.api.common.dto.FileUploadResponse;
import one.dfy.bily.api.common.dto.User;
import one.dfy.bily.api.common.service.FileService;
import one.dfy.bily.api.space.service.SpaceService;
import one.dfy.bily.api.util.S3Uploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private S3Uploader s3Uploader;
    @Autowired
    private FileService fileService;
    @Autowired
    private SpaceService spaceService;


    @Operation(
            summary = "이미지 업로드",
            description = "이미지 파일과 제목을 함께 업로드합니다."
    )
    @ApiResponse(responseCode = "200", description = "업로드 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> upload(
            @RequestParam("content_id") Long contentId,
            @RequestPart(value = "spaceImages", required = false) List<MultipartFile> spaceImages,
            @RequestPart(value = "titles", required = false) List<String> titles
    ) {
        try {
            if (spaceImages == null || spaceImages.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "파일이 포함되지 않았습니다."));
            }

            if (titles == null) {
                titles = new ArrayList<>(Collections.nCopies(spaceImages.size(), "")); // 제목이 없으면 빈 문자열로 채움
            }

            if (spaceImages.size() != titles.size()) {
                return ResponseEntity.badRequest().body(Map.of("message", "파일 개수와 제목 개수가 일치하지 않습니다."));
            }

            for (int i = 0; i < spaceImages.size(); i++) {
                MultipartFile file = spaceImages.get(i);
                String title = titles.get(i);

                String fileName = file.getOriginalFilename();
                long fileSize = file.getSize();

                s3Uploader.upload(contentId, fileSize, fileName, file, title);
            }

            return ResponseEntity.ok(Map.of("message", "업로드 성공"));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "업로드 실패: " + e.getMessage()));
        }
    }



    @GetMapping("/uploaded-files")
    public ResponseEntity<List<FileInfoEntity>> getUploadedFiles(@RequestParam("content_id") Long contentId) {
        List<FileInfoEntity> uploadedFiles = fileService.getFileList(contentId);
        return ResponseEntity.ok().body(uploadedFiles);
    }

    @PostMapping("/upload/business-card/{userId}")
    public ResponseEntity<FileUploadResponse> uploadUserBusinessCard(
            @PathVariable Long userId,
            @RequestPart MultipartFile multipartFile
    ) throws IOException {

        String fileName = multipartFile.getOriginalFilename();
        String dirName = "business-card";  // 명함 이미지 저장 디렉토리
        long fileSize = multipartFile.getSize();

        FileUploadResponse response = s3Uploader.uploadBusinessCard(userId, fileSize, fileName, multipartFile, dirName);

        return ResponseEntity.ok(response);
    }

//    @GetMapping("/profile")
//    public ResponseEntity<User> getProfile(@RequestParam("userId") Long userId) {
//        if (userId == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        Optional<User> user = userServiceTrash.getUserById(userId);
//        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
//    }


/*    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(@RequestBody User user, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId"); // 요청에서 userId 추출

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        user.setUserId(userId);
        userService.updateUser(user);
        Optional<User> updatedUser = userService.getUserById(userId);
        return updatedUser.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }*/

}
