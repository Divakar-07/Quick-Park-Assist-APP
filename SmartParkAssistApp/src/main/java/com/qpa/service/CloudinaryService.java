package com.qpa.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import jakarta.annotation.PostConstruct;

@Service
public class CloudinaryService {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    private Cloudinary cloudinary;

    @PostConstruct
    public void init() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true));
    }

    public String uploadImage(MultipartFile file, String folder, String identifier) throws IOException {
        validateImage(file);

        String publicId = generatePublicId(folder, identifier);
        Map<String, Object> params = ObjectUtils.asMap(
                "public_id", publicId,
                "folder", folder != null ? folder : "default",
                "overwrite", true,
                "resource_type", "auto");

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
        return (String) uploadResult.get("secure_url");
    }

    public String uploadImage(MultipartFile file) throws IOException {
        return uploadImage(file, null, null);
    }

    public String uploadSpotImage(MultipartFile file, Long spotId, String existingImageUrl) {
        try {
            // Delete existing image if provided
            if (existingImageUrl != null && !existingImageUrl.isEmpty()) {
                deleteImage(existingImageUrl);
            }

            // Upload new spot image
            String publicId = "spots/" + spotId + "_spot_" + generateUniqueId();
            return uploadImage(file, "spots", publicId);
        } catch (IOException e) {
            throw new RuntimeException("Spot image upload failed", e);
        }
    }

    public void deleteImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                String publicId = extractPublicIdFromUrl(imageUrl);
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            } catch (IOException e) {
                throw new RuntimeException("Image deletion failed", e);
            }
        }
    }

    private String extractPublicIdFromUrl(String url) {
        // Remove query parameters if present
        int queryIndex = url.indexOf("?");
        if (queryIndex != -1) {
            url = url.substring(0, queryIndex);
        }

        String[] urlParts = url.split("/");
        String fileNameWithExtension = urlParts[urlParts.length - 1];
        return fileNameWithExtension.substring(0, fileNameWithExtension.lastIndexOf("."));
    }

    private String generatePublicId(String folder, String identifier) {
        String basePath = folder != null ? folder + "/" : "";
        String uniqueId = generateUniqueId();
        return basePath + (identifier != null ? identifier + "_" : "") + uniqueId;
    }

    private String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    private void validateImage(MultipartFile file) {
        // Maximum file size: 10MB
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("Image must be less than 10MB");
        }

        // Allowed image types
        String[] allowedTypes = { "image/jpeg", "image/png", "image/webp" };
        String contentType = file.getContentType();
        if (contentType == null || !Arrays.asList(allowedTypes).contains(contentType)) {
            throw new IllegalArgumentException("Invalid file type. Only JPEG, PNG, and WebP are allowed");
        }
    }
}