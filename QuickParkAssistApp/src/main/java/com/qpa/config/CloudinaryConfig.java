package com.qpa.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

/**
 * Configuration class for Cloudinary setup.
 * This class initializes the Cloudinary service with the necessary credentials.
 */
@Configuration
public class CloudinaryConfig {

    // Cloudinary cloud name retrieved from application properties
    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    // Cloudinary API key retrieved from application properties
    @Value("${cloudinary.api-key}")
    private String apiKey;

    // Cloudinary API secret retrieved from application properties
    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    /**
     * Bean definition for Cloudinary instance.
     * Initializes Cloudinary with the configured credentials.
     * 
     * @return Configured Cloudinary instance.
     */
    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        return new Cloudinary(config);
    }
}
