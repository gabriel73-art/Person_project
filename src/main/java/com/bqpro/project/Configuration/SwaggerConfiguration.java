package com.bqpro.project.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;


import java.util.Collections;
//@EnableSwagger2

@Configuration
public class SwaggerConfiguration {

    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.OAS_30)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.bqpro.project.Controller"))
                .paths(PathSelectors.any())
                .build()
                .pathMapping("/")
                .apiInfo(apiInfo());

    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "ABC Bank API",
                "Api to manage clients",
                "1.0",
                "Terms of service",
                new Contact("Gabriel Monteagudo", "https://github.com/gabriel73-art", "gabriel.monteagudo@bestvision.group"),
                "",
                "",
                Collections.emptyList());
    }

}
