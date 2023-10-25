package com.anyi.reggie.config;

/**
 * @author 安逸i
 * @version 1.0
 */

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author 安逸i
 * @version 1.0
 * 这个swagger是可以暴露出这个项目所有的api接口的,但是不知道为什么运行不了
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket webApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("webApi")
                .apiInfo(webApiInfo())
                .select()
                // https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api
                // After defining the Docket bean, its select() method returns an instance of ApiSelectorBuilder,
                // which provides a way to control the endpoints exposed by Swagger.
                // We can configure predicates for selecting RequestHandlers with the help of RequestHandlerSelectors and PathSelectors. Using any() for both will make documentation for our entire API available through Swagger.
                .paths(Predicates.not(PathSelectors.regex("/admin/.*")))
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .build();
    }


    private ApiInfo webApiInfo(){
        return new ApiInfoBuilder()
                .title("网站-课程中心API文档")
                .description("本文档描述了课程中心微服务接口定义")
                .version("1.0")
                .contact(new Contact("Helen", "www.ease.center",
                        "1961741003@qq.com"))
                .build();
    }
}