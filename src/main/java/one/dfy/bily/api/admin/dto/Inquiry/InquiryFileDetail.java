package one.dfy.bily.api.admin.dto.Inquiry;

public record InquiryFileDetail(
        String originalFileName,
        String newFileName,
        String extension,
        String saveLocation,
        long fileSize,
        String fileType
) {
}
