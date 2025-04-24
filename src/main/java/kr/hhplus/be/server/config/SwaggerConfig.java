package kr.hhplus.be.server.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openApi(){
        return new OpenAPI()
                .info(new Info() //info 객체 생성
                        .title("콘서트 예약 서비스 api") // api 타이틀
                        .description("콘서트 시스템 api 명세서입니다.") // api 설명
                        .version("v1.0.0")  // api 버젼 정보
                        .license(new License().name("MIT Licence").url("https://opensource.org/licenses/MIT")) // 라이선스 정보
                        .contact(new Contact() // 연락처 정보 생성
                                .name("Larry")  //
                                .url("https://github.com/LK920")
                                .email("hopeone1214@gmail.com")
                        )
                );
    }
}
