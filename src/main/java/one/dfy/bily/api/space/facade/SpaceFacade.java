package one.dfy.bily.api.space.facade;

import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.common.annotation.Facade;
import one.dfy.bily.api.space.service.SpaceService;
import one.dfy.bily.api.user.service.UserService;

@Facade
@RequiredArgsConstructor
public class SpaceFacade {
    private final SpaceService spaceService;
    private final UserService userService;


}
