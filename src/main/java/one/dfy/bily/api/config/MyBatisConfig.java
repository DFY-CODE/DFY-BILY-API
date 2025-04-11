package one.dfy.bily.api.config;

import one.dfy.bily.api.common.handler.ListToStringTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@MapperScan("one.dfy.bily.api.common.mapper")
public class MyBatisConfig {

    @Bean
    public ConfigurationCustomizer myBatisConfigurationCustomizer() {
        return configuration -> configuration.getTypeHandlerRegistry().register(List.class, JdbcType.VARCHAR, new ListToStringTypeHandler());
    }
}

