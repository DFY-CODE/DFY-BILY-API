package one.dfy.bily.api;


import org.mybatis.spring.annotation.MapperScan;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "one.dfy.bily.api")@MapperScan(basePackages = {
        "one.dfy.bily.api.common.mapper",
        "one.dfy.bily.api.space.mapper",
        "one.dfy.bily.api.chat.mapper"
})
public class OneDfyBilyApiApplication {

    // .env 파일 로드 및 환경 변수 설정 메서드 분리 (가독성 향상)
    private static void loadEnvironmentVariables() {
        Dotenv dotenv = Dotenv.configure()
                .directory("") // 클래스패스 루트에서 찾도록 디렉토리 설정 제거 또는 빈 문자열 지정
                .ignoreIfMissing() // 파일이 없어도 예외 발생시키지 않음 (선택 사항)
                .load();

        System.setProperty("AWS_S3_BUCKET", dotenv.get("AWS_S3_BUCKET"));
        System.setProperty("AWS_ACCESS_KEY", dotenv.get("AWS_ACCESS_KEY"));
        System.setProperty("AWS_SECRET_KEY", dotenv.get("AWS_SECRET_KEY"));
        System.setProperty("AWS_REGION", dotenv.get("AWS_REGION"));

        System.setProperty("MAIL_USERNAME", dotenv.get("MAIL_USERNAME"));
        System.setProperty("MAIL_PASSWORD", dotenv.get("MAIL_PASSWORD"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));

    }

    public static void main(String[] args) {
        loadEnvironmentVariables(); // 환경 변수 로드
        SpringApplication.run(OneDfyBilyApiApplication.class, args);
    }

}
