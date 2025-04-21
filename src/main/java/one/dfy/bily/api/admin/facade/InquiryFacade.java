package one.dfy.bily.api.admin.facade;

import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.admin.dto.InquiryResponse;
import one.dfy.bily.api.admin.dto.InquiryUpdateRequest;
import one.dfy.bily.api.admin.model.space.Space;
import one.dfy.bily.api.admin.service.InquiryService;
import one.dfy.bily.api.common.annotation.Facade;
import one.dfy.bily.api.admin.service.SpaceService;

import java.util.Optional;

@Facade
@RequiredArgsConstructor
public class InquiryFacade {
    private final InquiryService inquiryService;
    private final SpaceService spaceService;

    public InquiryResponse updateInquiry(Long inquiryId, InquiryUpdateRequest inquiryUpdateRequest){
        Space space = spaceService.findById(inquiryUpdateRequest.spaceId());

        return inquiryService.updateInquiry(inquiryId, inquiryUpdateRequest, space);
    }
}
