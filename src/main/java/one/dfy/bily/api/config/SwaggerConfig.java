package one.dfy.bily.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("BILY SEOUL API 문서")
                .version("v1.0")
                .description("BILY SEOUL API 문서입니다.");
        return new OpenAPI().info(info);
    }
}
