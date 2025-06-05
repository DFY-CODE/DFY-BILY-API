package one.dfy.bily.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new ParameterNamesModule()); // Java Record 지원
        mapper.registerModule(new JavaTimeModule());       // LocalDateTime 지원
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO 포맷 사용
        return mapper;
    }

}

