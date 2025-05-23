package one.dfy.bily.api.space.dto;

public record SpaceUseFileResponse(
        Long id,
        String fileUrl,
        int fileOrder,
        String fileTitle
){
}
