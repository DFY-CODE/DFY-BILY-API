package one.dfy.bily.api.space.dto;

import one.dfy.bily.api.common.dto.Pagination;

import java.util.List;

public record NonUserSpaceInfoResponse(
        List<NonUserSpaceInfo> nonUserSpaceInfoList,
        Pagination pagination
) {
}
