package one.dfy.bily.api.space.dto;

public record SpaceFileUpdateRequest(
        Long id,
        boolean deleteStatus,
        int fileOrder
) {
}
