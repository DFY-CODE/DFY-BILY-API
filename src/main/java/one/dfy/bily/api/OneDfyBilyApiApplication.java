package one.dfy.bily.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "one.dfy.bily.api")
@MapperScan("one.dfy.bily.api.common.mapper") // 매퍼 패키지 지정
public class OneDfyBilyApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(OneDfyBilyApiApplication.class, args);
    }

}
