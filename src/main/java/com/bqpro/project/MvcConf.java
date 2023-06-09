package com.bqpro.project;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConf implements WebMvcConfigurer {

    public void addResourceHandlers(ResourceHandlerRegistry registry){
        WebMvcConfigurer.super.addResourceHandlers(registry);
        exposeDirectory("person-images", registry);
        // registry.addResourceHandler("/imagenEcc/**").addResourceLocations("file:/C:/imagenEcc/");
    }

    private void exposeDirectory(String dirName, ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get(dirName);
        String uploadPath = uploadDir.toFile().getAbsolutePath();
        if (dirName.startsWith("../")) dirName = dirName.replace("../", "");

        String systemName = System.getProperties().get("os.name").toString();
        //Check if run system is windows or not for set path
        if(systemName.contains("Windows")){
            registry.addResourceHandler("/" + dirName + "/**").addResourceLocations("file:/"+ uploadPath + "/");
        }else{
            registry.addResourceHandler("/" + dirName + "/**").addResourceLocations("file:///"+ uploadPath + "/");
        }
    }

   }

