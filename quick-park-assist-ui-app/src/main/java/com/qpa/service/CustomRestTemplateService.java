package com.qpa.service;

import java.io.IOException;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qpa.dto.ResponseDTO;
import com.qpa.entity.AuthUser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class CustomRestTemplateService {

    private final RestTemplate restTemplate;
    private static final String BASE_URL = "http://localhost:7212/api";

    public CustomRestTemplateService() {
        this.restTemplate = new RestTemplate();
        this.restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    }

    private HttpHeaders prepareHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String cookie = request.getHeader(HttpHeaders.COOKIE);
        if (cookie != null) {
            headers.add(HttpHeaders.COOKIE, cookie);
        }
        return headers;
    }

    private void storeSessionCookie(HttpHeaders backendHeaders, HttpServletResponse response) {
        List<String> cookies = backendHeaders.get(HttpHeaders.SET_COOKIE);
        if (cookies != null) {
            for (String cookie : cookies) {
                response.addHeader(HttpHeaders.SET_COOKIE, cookie);
            }
        }
    }

    public <T> ResponseEntity<T> post(
            String route, Object requestBody, HttpServletRequest request,
            ParameterizedTypeReference<T> responseType) {

        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, prepareHeaders(request));

        try {
            return restTemplate.exchange(
                    BASE_URL + route,
                    HttpMethod.POST,
                    requestEntity,
                    responseType);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            return handleErrorResponse(ex, responseType);
        }
    }

    public <T> ResponseEntity<T> get(
            String route, HttpServletRequest request,
            ParameterizedTypeReference<T> responseType) {

        HttpEntity<Void> requestEntity = new HttpEntity<>(prepareHeaders(request));

        try {
            return restTemplate.exchange(
                    BASE_URL + route,
                    HttpMethod.GET,
                    requestEntity,
                    responseType);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            return handleErrorResponse(ex, responseType);
        }
    }

    public <T> ResponseEntity<T> put(
            String route, Object requestBody, HttpServletRequest request,
            ParameterizedTypeReference<T> responseType) {

        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, prepareHeaders(request));

        try {
            return restTemplate.exchange(
                    BASE_URL + route,
                    HttpMethod.PUT,
                    requestEntity,
                    responseType);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            return handleErrorResponse(ex, responseType);
        }
    }
    public <T> ResponseEntity<T> delete(
            String route, Object requestBody, HttpServletRequest request,
            ParameterizedTypeReference<T> responseType) {

        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, prepareHeaders(request));

        try {
            return restTemplate.exchange(
                    BASE_URL + route,
                    HttpMethod.DELETE,
                    requestEntity,
                    responseType);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            return handleErrorResponse(ex, responseType);
        }
    }

    private <T> ResponseEntity<T> handleErrorResponse(HttpStatusCodeException ex,
            ParameterizedTypeReference<T> responseType) {

        // Extract meaningful message from response body
        String responseBody = ex.getResponseBodyAsString();
        System.out.println("error log1 :" + responseBody);
        String meaningfulMessage = extractErrorMessage(responseBody);
        System.out.println("error log2 meaningfulMessage :" + meaningfulMessage);
        ResponseDTO<String> errorResponse = new ResponseDTO<>(meaningfulMessage, ex.getStatusCode().value(), false);
        return ResponseEntity.status(ex.getStatusCode()).body((T) errorResponse);
    }

    // Method to extract error message from JSON response
    private String extractErrorMessage(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.has("message") ? jsonNode.get("message").asText() : "An unexpected error occurred.";
        } catch (JsonProcessingException e) {
            return "An unexpected error occurred.";
        }
    }

    public <T> ResponseEntity<T> postMultipart(
            String route, MultipartFile file, HttpServletRequest request,
            ParameterizedTypeReference<T> responseType) throws IOException {

        HttpHeaders headers = prepareHeaders(request);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new org.springframework.core.io.ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            return restTemplate.exchange(BASE_URL + route, HttpMethod.POST, requestEntity, responseType);
        } catch (RestClientException ex) {
            ResponseDTO<String> errorResponse = new ResponseDTO<>("Error uploading file", 500, false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((T) errorResponse);
        }
    }

    public <T> ResponseEntity<T> login(String route,
            AuthUser loginRequest, HttpServletRequest request, HttpServletResponse response,
            ParameterizedTypeReference<T> responseType) {

        HttpEntity<Object> requestEntity = new HttpEntity<>(loginRequest, prepareHeaders(request));

        try {
            ResponseEntity<T> backendResponse = restTemplate.exchange(
                    BASE_URL + route,
                    HttpMethod.POST,
                    requestEntity,
                    responseType);

            storeSessionCookie(backendResponse.getHeaders(), response);

            return backendResponse;
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            T responseError = ex.getResponseBodyAs(responseType);
            if (responseError == null) {
                return ResponseEntity.status(401)
                        .body((T) new ResponseDTO<>("Invalid Credentials", 401, false));
            }

            return handleErrorResponse(ex, responseType);
        }
    }

    public <T> ResponseEntity<T> logout(
            HttpServletRequest request, HttpServletResponse response, ParameterizedTypeReference<T> responseType) {

        HttpEntity<Void> requestEntity = new HttpEntity<>(prepareHeaders(request));

        try {
            ResponseEntity<T> backendResponse = restTemplate.exchange(
                    BASE_URL + "/auth/logout",
                    HttpMethod.POST,
                    requestEntity,
                    responseType);

            response.setHeader(HttpHeaders.SET_COOKIE, "jwt=; Path=/; HttpOnly; Max-Age=0");
            return backendResponse;
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            return handleErrorResponse(ex, responseType);
        }
    }


}
