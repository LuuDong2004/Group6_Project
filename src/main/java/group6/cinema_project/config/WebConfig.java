package group6.cinema_project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/")
                .setCachePeriod(3600)
                .resourceChain(true);

        // Add resource handler for uploaded files
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:src/main/resources/static/uploads/")
                .setCachePeriod(3600);
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.mediaType("css", MediaType.valueOf("text/css"));
        configurer.mediaType("js", MediaType.valueOf("application/javascript"));
        configurer.mediaType("png", MediaType.valueOf("image/png"));
        configurer.mediaType("jpg", MediaType.valueOf("image/jpeg"));
        configurer.mediaType("jpeg", MediaType.valueOf("image/jpeg"));
        configurer.mediaType("gif", MediaType.valueOf("image/gif"));
        configurer.mediaType("svg", MediaType.valueOf("image/svg+xml"));
    }
} 