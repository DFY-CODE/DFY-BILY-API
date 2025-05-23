package one.dfy.bily.api.inquiry.mapper;

import one.dfy.bily.api.inquiry.constant.InquiryStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class InquiryStatusConverter implements Converter<String, InquiryStatus> {

    @Override
    public InquiryStatus convert(String source) {
        return InquiryStatus.fromDescription(source);
    }
}