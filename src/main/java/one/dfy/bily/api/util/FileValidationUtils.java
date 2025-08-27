package one.dfy.bily.api.util;

import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class FileValidationUtils {

    private static final long MAX_UPLOAD_SIZE = 5 * 1024 * 1024; // 5 MB

    private FileValidationUtils() { }

    /** 업로드 대상인지 여부(널 X, isEmpty X, 크기 > 0) */
    public static boolean isValid(MultipartFile file) {
        return file != null && !file.isEmpty() && file.getSize() > 0;
    }

    /** 리스트에서 유효한 파일만 추출 */
    public static List<MultipartFile> filterValid(List<MultipartFile> files) {
        return files == null ? Collections.emptyList()
                : files.stream()
                .filter(FileValidationUtils::isValid)
                .collect(Collectors.toList());
    }

    /** 최대 용량 체크(초과 시 IllegalArgumentException) */
    public static void checkMaxSize(MultipartFile file) {
        if (file != null && file.getSize() > MAX_UPLOAD_SIZE) {
            throw new IllegalArgumentException(
                    "허용 용량(" + MAX_UPLOAD_SIZE + "B)을 초과한 파일: " + file.getOriginalFilename());
        }
    }
}

