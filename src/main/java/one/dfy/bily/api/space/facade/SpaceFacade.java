package one.dfy.bily.api.space.facade;

import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.common.annotation.Facade;
import one.dfy.bily.api.common.dto.Pagination;
import one.dfy.bily.api.space.dto.AdminSpaceInfo;
import one.dfy.bily.api.space.dto.AdminSpaceInfoList;
import one.dfy.bily.api.space.mapper.SpaceDtoMapper;
import one.dfy.bily.api.space.model.Space;
import one.dfy.bily.api.space.service.SpaceService;
import one.dfy.bily.api.user.model.User;
import one.dfy.bily.api.user.service.UserService;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Facade
@RequiredArgsConstructor
public class SpaceFacade {
    private final SpaceService spaceService;
    private final UserService userService;

    public AdminSpaceInfoList findAdminSpaceInfoList(String spaceAlias, Boolean displayStatus, int page, int size) {
        Page<Space> spacePage = spaceService.findAdminSpaceInfoList(spaceAlias, displayStatus, page, size);
        List<Long> userIds = spacePage.getContent().stream().map(Space::getId).toList();

        Map<Long, String> userList = userService.findByIds(userIds);

        List<AdminSpaceInfo> spaceInfoList =spacePage.getContent().stream()
                        .map(space -> {
                            try {
                                return SpaceDtoMapper.toAdminSpaceInfo(space, userList);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        })
                                .toList();

        return new AdminSpaceInfoList(spaceInfoList, new Pagination(page, size, spacePage.getTotalElements(), spacePage.getTotalPages()));
    }

}
