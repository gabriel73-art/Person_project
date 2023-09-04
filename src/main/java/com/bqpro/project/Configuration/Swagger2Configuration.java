package com.bqpro.project.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@EnableSwagger2
@Configuration
@OpenAPIDefinition(info = @Info(
        title = "My REST API",
        description = "Some custom description of API.",
        version = "1.0",
        contact = @io.swagger.v3.oas.annotations.info.Contact(
                name = "Gabriel Monteagudo",
                url = "www.example.com",
                email = "gabriel.monteagudo@bestvision.group"
        ),
        license = @io.swagger.v3.oas.annotations.info.License(
                name = "License of API",
                url = "API license URL"
        )
))
public class Swagger2Configuration {

    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.bqpro.project.Controller"))
                .paths(PathSelectors.ant("/persons/*"))
                .build();
    }

   /* private ApiInfo apiInfo() {
        return new ApiInfo(
                "My REST API",
                "Some custom description of API.",
                "API TOS",
                "Terms of service",
                new Contact("Gabriel Monteagudo", "www.example.com", "gabriel.monteagudo@bestvision.group"),
                "License of API", "API license URL", Collections.emptyList());
    }*/
}