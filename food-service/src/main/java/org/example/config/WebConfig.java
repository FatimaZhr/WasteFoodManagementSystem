package org.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // This tells Spring: "If someone asks for /uploads/..., look in the 'uploads' folder on the computer"
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}