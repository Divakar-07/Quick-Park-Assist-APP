package com.qpa.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.qpa.dto.AdminSpotsStatistics;
import com.qpa.dto.ResponseDTO;
import com.qpa.dto.SpotResponseDTO;
import com.qpa.entity.PaymentUI;
import com.qpa.entity.SpotBookingInfo;
import com.qpa.entity.UserInfo;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserService {

    @Autowired
    private CustomRestTemplateService restTemplate;

    /**
     * Fetches user details.
     */
    public ResponseDTO<UserInfo> getUserDetails(HttpServletRequest request) {
        return restTemplate.get("/users/", request, new ParameterizedTypeReference<ResponseDTO<UserInfo>>() {
        }).getBody();
    }

    /**
     * Uploads user avatar.
     */
    public ResponseDTO<Void> uploadImage(MultipartFile file, HttpServletRequest request) {
        try {
            return restTemplate.postMultipart("/users/upload-avatar", file, request,
                    new ParameterizedTypeReference<ResponseDTO<Void>>() {
                    }).getBody();
        } catch (IOException e) {
            return new ResponseDTO<>(e.getMessage(), 500, false);
        }
    }

    /**
     * Updates user information.
     */
    public ResponseDTO<Void> updateUser(UserInfo user, HttpServletRequest request) {
        return restTemplate.put("/users/update", user, request, new ParameterizedTypeReference<ResponseDTO<Void>>() {
        }).getBody();
    }

    public ResponseDTO<List<SpotBookingInfo>> getUserBookingHistory(HttpServletRequest request) {
        ResponseDTO<List<SpotBookingInfo>> listData = restTemplate
                .get("/users/booking-history", request,
                        new ParameterizedTypeReference<ResponseDTO<List<SpotBookingInfo>>>() {
                        })
                .getBody();

        List<SpotBookingInfo> bookingList = listData.getData();
        if (bookingList != null) {
            System.out.println("Spot Booking Details:");
            for (SpotBookingInfo booking : bookingList) {
                System.out.println("-------------------------------");
                System.out.println(booking);
            }
            System.out.println("-------------------------------");
            System.out.println();
        }
        return listData;

    }

    public ResponseDTO<List<UserInfo>> getAllUsers(HttpServletRequest request) {
        return restTemplate
                .get("/users/getAllUsers", request, new ParameterizedTypeReference<ResponseDTO<List<UserInfo>>>() {
                }).getBody();
    }

    public ResponseDTO<List<UserInfo>> getActiveUsersForAdminParkingSpots(HttpServletRequest request) {
        return restTemplate
                .get("/users/spots/current-active", request,
                        new ParameterizedTypeReference<ResponseDTO<List<UserInfo>>>() {
                        })
                .getBody();
    }

    public List<SpotResponseDTO> getAdminSpots(HttpServletRequest request) {
        UserInfo user = getUserDetails(request).getData();
        return restTemplate.get("/spots/owner?userId=" + user.getUserId(), request,
                new ParameterizedTypeReference<List<SpotResponseDTO>>() {
                }).getBody();
    }

    public List<PaymentUI> getAllAdminPayments(HttpServletRequest request) {
        return restTemplate
                .get("/payments/getAllAdminPayments", request, new ParameterizedTypeReference<List<PaymentUI>>() {
                }).getBody();
    }

    public UserInfo getUserByUserId(Long userId, HttpServletRequest request) {
        return restTemplate.get("/users/" + userId, request, new ParameterizedTypeReference<UserInfo>() {
        }).getBody();
    }

    public SpotBookingInfo getBookingByVehicleId(Long vehicleId, HttpServletRequest request) {
        return restTemplate
                .get("/bookSlot/vehicleId=" + vehicleId, request, new ParameterizedTypeReference<SpotBookingInfo>() {
                }).getBody();
    }

    public PaymentUI getPaymentByBookingId(Long bookingId, HttpServletRequest request) {
        return restTemplate
                .get("/payments/viewByBookingId/" + bookingId, request, new ParameterizedTypeReference<PaymentUI>() {
                }).getBody();
    }

    public List<SpotBookingInfo> getBookingsForAdmin(HttpServletRequest request) {
        Long userId = getUserDetails(request).getData().getUserId();
        return restTemplate
                .get("/bookSlot/admin/" + userId, request, new ParameterizedTypeReference<List<SpotBookingInfo>>() {
                }).getBody();
    }

    public SpotBookingInfo getBookingByBookingId(Long bookingId, HttpServletRequest request) {

        return restTemplate
                .get("/bookSlot/viewBookingById/" + bookingId, request,
                        new ParameterizedTypeReference<SpotBookingInfo>() {
                        })
                .getBody();
    }

    public AdminSpotsStatistics getAdminSpotsStatistics(HttpServletRequest request){
        
        Long userId = getUserDetails(request).getData().getUserId();

        return restTemplate
        .get("/spots/getAdminSpotStatistics/" + userId, request,
                new ParameterizedTypeReference<AdminSpotsStatistics>() {
                })
        .getBody();
    }
}
