package com.bookstore.backend.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.lang.NonNull;

@Configuration
public class WebConfig implements WebMvcConfigurer {

@Override
public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {

    Path uploadDir = Paths.get(System.getProperty("user.dir")).resolve("uploads");
    String uploadPath = uploadDir.toFile().getAbsolutePath();

    registry.addResourceHandler("/uploads/**")
            .addResourceLocations("file:" + uploadPath + "/")
            .setCachePeriod(0);
}

}



