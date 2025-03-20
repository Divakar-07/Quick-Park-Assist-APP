package com.qpa.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/api/**")  // 🔥 Allow all API routes
                        .allowedOrigins("http://localhost:5001")  // 🔥 Allow frontend
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 🔥 Allow these HTTP methods
                        .allowedHeaders("*")  // 🔥 Allow all headers
                        .allowCredentials(true);  // 🔥 Allow cookies/session data
            }
        };
    }
}
