package one.dfy.bily.api.inquiry.facade;

import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.inquiry.dto.InquiryCreateRequest;
import one.dfy.bily.api.inquiry.dto.InquiryResponse;
import one.dfy.bily.api.inquiry.dto.InquiryUpdateRequest;
import one.dfy.bily.api.space.model.Space;
import one.dfy.bily.api.inquiry.service.InquiryService;
import one.dfy.bily.api.common.annotation.Facade;
import one.dfy.bily.api.space.service.SpaceService;

@Facade
@RequiredArgsConstructor
public class InquiryFacade {
    private final InquiryService inquiryService;
    private final SpaceService spaceService;

    public InquiryResponse updateInquiry(Long inquiryId, InquiryUpdateRequest inquiryUpdateRequest){
        Space space = spaceService.findById(inquiryUpdateRequest.spaceId());

        return inquiryService.updateInquiry(inquiryId, inquiryUpdateRequest, space);
    }

    public InquiryResponse createInquiry(InquiryCreateRequest request, Long userId) {
        Space space = spaceService.findById(request.spaceId());

        return inquiryService.createInquiry(request,space,userId);
    }
}
