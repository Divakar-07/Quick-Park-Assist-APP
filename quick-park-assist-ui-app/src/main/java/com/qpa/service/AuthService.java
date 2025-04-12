package com.qpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.qpa.dto.RegisterDTO;
import com.qpa.dto.ResponseDTO;
import com.qpa.entity.AuthUser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthService {

    @Autowired
    private CustomRestTemplateService restTemplateService;

    /**
     * ✅ Checks if the user is authenticated by calling the backend authentication
     * check API.
     *
     * @param request HTTP request containing cookies/session details
     * @return true if authenticated, false otherwise
     */
    public boolean isAuthenticated(HttpServletRequest request) {
        try {
            ResponseEntity<ResponseDTO<Void>> response = restTemplateService.get(
                    "/auth/check", request, new ParameterizedTypeReference<ResponseDTO<Void>>() {
                    });

            ResponseDTO<Void> responseData = response.getBody();
            return responseData != null && responseData.isSuccess();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * ✅ Registers a new user by sending the registration details to the backend
     * API.
     *
     * @param registerDTO The user's registration details
     * @param request     HTTP request containing cookies/session details
     * @return ResponseDTO indicating the success or failure of the registration
     */
    public ResponseDTO<Void> registerUser(RegisterDTO registerDTO, HttpServletRequest request) {
        ResponseEntity<ResponseDTO<Void>> responseEntity = restTemplateService.post(
                "/users/register", registerDTO, request, new ParameterizedTypeReference<ResponseDTO<Void>>() {
                });
        return responseEntity.getBody();
    }

    /**
     * ✅ Logs in the user by sending authentication details to the backend.
     * Stores the authentication cookies in the HTTP response.
     *
     * @param authUser The user authentication details
     * @param response HTTP response where authentication cookies will be stored
     * @param request  HTTP request containing cookies/session details
     * @return ResponseDTO indicating the success or failure of the login
     */
    public ResponseDTO<Void> login(AuthUser authUser, HttpServletResponse response, HttpServletRequest request) {
        ResponseEntity<ResponseDTO<Void>> backendResponse = restTemplateService.login("/auth/login", authUser, request,
                response, new ParameterizedTypeReference<ResponseDTO<Void>>() {
                });
        return backendResponse.getBody();
    }

    /**
     * ✅ Logs out the user by calling the backend logout API and clearing cookies.
     *
     * @param request  HTTP request containing cookies/session details
     * @param response HTTP response where authentication cookies will be cleared
     * @return ResponseDTO indicating the success or failure of the logout
     */
    public ResponseDTO<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        return restTemplateService.logout(request, response, new ParameterizedTypeReference<ResponseDTO<Void>>() {
        }).getBody();
    }

}
