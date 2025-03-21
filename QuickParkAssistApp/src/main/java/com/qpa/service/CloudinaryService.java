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

    /**
     * Uploads an image file to Cloudinary and returns the URL of the uploaded image.
     *
     * @param file the image file to be uploaded
     * @return the URL of the uploaded image
     * @throws IOException if an error occurs during file upload
     */
    @SuppressWarnings("unchecked")
    public String uploadImage(MultipartFile file) throws IOException {
        // Upload the image to Cloudinary
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        
        // Return the URL of the uploaded image
        return (String) uploadResult.get("url");
    }

    /**
     * Extracts the public ID from a given Cloudinary image URL.
     *
     * @param imageUrl the URL of the image
     * @return the extracted public ID, or null if extraction fails
     */
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
        
        // Validate the extracted indices
        if (lastSlashIndex == -1 || extensionIndex == -1 || extensionIndex < lastSlashIndex) {
            return null;
        }

        // Extract and return the public ID (without file extension)
        return imageUrl.substring(lastSlashIndex + 1, extensionIndex);
    }

    /**
     * Deletes an image from Cloudinary using its URL.
     *
     * @param imageUrl the URL of the image to be deleted
     * @throws IOException if an error occurs during deletion
     * @throws IllegalArgumentException if the image URL is invalid
     */
    public void deleteImage(String imageUrl) throws IOException {
        // Extract the public ID from the image URL
        String publicId = extractPublicId(imageUrl);
        
        if (publicId != null && !publicId.isEmpty()) {
            // Delete the image from Cloudinary using the extracted public ID
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } else {
            throw new IllegalArgumentException("Invalid Image URL: Cannot extract public ID");
        }
    }
}