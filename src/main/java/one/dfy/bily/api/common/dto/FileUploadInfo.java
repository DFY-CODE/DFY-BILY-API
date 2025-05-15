package one.dfy.bily.api.common.dto;

public record FileUploadInfo(
        String originalFileName,
        String newFileName,
        String extension,
        String saveLocation,
        long fileSize,
        String fileType
) {
}
