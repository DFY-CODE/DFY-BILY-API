package one.dfy.bily.api.user.dto;

import one.dfy.bily.api.common.dto.Pagination;

import java.util.List;

public record UserInfoList(
        List<UserInfo> userInfoList,
        Pagination pagination
) {
}
