package one.dfy.bily.api.admin.mapper;

import one.dfy.bily.api.admin.constant.InquirySearchType;
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
