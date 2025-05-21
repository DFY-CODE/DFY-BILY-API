package one.dfy.bily.api.space.dto;

public record SpaceFileInfoResponse(
        Long id,
        String fileUrl,
        boolean isThumbnail,
        int fileOrder
) {
}
