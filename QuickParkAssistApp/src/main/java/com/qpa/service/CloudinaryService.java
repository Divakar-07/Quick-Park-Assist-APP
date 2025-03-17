package com.qpa.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) throws IOException {
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return (String) uploadResult.get("url");  // Returns the uploaded image URL
    }

    public static String extractPublicId(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }
        
        // Remove query parameters if present
        int queryIndex = imageUrl.indexOf("?");
        if (queryIndex != -1) {
            imageUrl = imageUrl.substring(0, queryIndex);
        }

        // Find the last '/' before the file extension
        int lastSlashIndex = imageUrl.lastIndexOf("/");
        int extensionIndex = imageUrl.lastIndexOf(".");
        
        if (lastSlashIndex == -1 || extensionIndex == -1 || extensionIndex < lastSlashIndex) {
            return null;
        }

        // Extract the publicId (without extension)
        return imageUrl.substring(lastSlashIndex + 1, extensionIndex);
    }

    public void deleteImage(String imageUrl) throws IOException {
        String publicId = extractPublicId(imageUrl);
        if (publicId != null && !publicId.isEmpty()) {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } else {
            throw new IllegalArgumentException("Invalid Image URL: Cannot extract public ID");
        }
    }
}
