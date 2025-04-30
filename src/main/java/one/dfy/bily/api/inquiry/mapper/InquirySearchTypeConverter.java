package one.dfy.bily.api.inquiry.mapper;

import one.dfy.bily.api.inquiry.constant.InquirySearchType;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class InquirySearchTypeConverter implements Converter<String, InquirySearchType> {
    @Override
    public InquirySearchType convert(@NotNull String source) {
        return InquirySearchType.fromDescription(source);
    }
}
