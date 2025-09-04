package com.redis.demo;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.session.web.http.DefaultCookieSerializer;

@EnableCaching
@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Redis Demo API", version = "v1"))
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public DefaultCookieSerializer cookieSerializer() {
        var s = new DefaultCookieSerializer();
        s.setUseBase64Encoding(false);
        s.setCookieName("SESSION");
        return s;
    }
}
