package one.dfy.bily.api.inquiry.facade;

import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.inquiry.dto.InquiryCreateRequest;
import one.dfy.bily.api.inquiry.dto.InquiryResponse;
import one.dfy.bily.api.inquiry.dto.InquiryUpdateRequest;
import one.dfy.bily.api.space.model.Space;
import one.dfy.bily.api.inquiry.service.InquiryService;
import one.dfy.bily.api.common.annotation.Facade;
import one.dfy.bily.api.space.service.SpaceService;
import one.dfy.bily.api.util.AES256Util;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Facade
@RequiredArgsConstructor
public class InquiryFacade {
    private final InquiryService inquiryService;
    private final SpaceService spaceService;

    public InquiryResponse updateInquiry(Long inquiryId, InquiryUpdateRequest inquiryUpdateRequest){

        Long spaceId = null;
        try {
            spaceId = AES256Util.decrypt(inquiryUpdateRequest.spaceId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Space space = spaceService.findById(spaceId);

        return inquiryService.updateInquiry(inquiryId, inquiryUpdateRequest, space);
    }

    public InquiryResponse createInquiry(InquiryCreateRequest request, List<MultipartFile> fileAttachments, Long userId) throws Exception {
        Space space = spaceService.findById(AES256Util.decrypt(request.spaceId()));

        return inquiryService.createInquiry(request, fileAttachments, space, userId);
    }
}
