package one.dfy.bily.api.component;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StringToIntegerListConverter implements Converter<String, List<Integer>> {
    @Override
    public List<Integer> convert(String source) {
        return Arrays.stream(source.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}

