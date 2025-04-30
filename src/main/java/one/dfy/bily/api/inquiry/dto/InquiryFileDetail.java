package one.dfy.bily.api.inquiry.dto;

public record InquiryFileDetail(
        String originalFileName,
        String newFileName,
        String extension,
        String saveLocation,
        long fileSize,
        String fileType
) {
}
