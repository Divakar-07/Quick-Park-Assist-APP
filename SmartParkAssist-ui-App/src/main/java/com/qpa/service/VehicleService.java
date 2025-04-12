package com.qpa.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.qpa.dto.ResponseDTO;
import com.qpa.entity.Vehicle;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class VehicleService {

    @Autowired
    private CustomRestTemplateService restTemplate;

    /**
     * Retrieves the list of vehicles associated with a user.
     */
    public ResponseDTO<List<Vehicle>> findUserVehicle(HttpServletRequest request) {
        return restTemplate
                .get("/vehicles/user", request, new ParameterizedTypeReference<ResponseDTO<List<Vehicle>>>() {
                }).getBody();
    }

    /**
     * Fetches a vehicle by its ID.
     */
    public ResponseDTO<Vehicle> getVehicleById(Long id, HttpServletRequest request) {
        return restTemplate.get("/vehicles/" + id, request, new ParameterizedTypeReference<ResponseDTO<Vehicle>>() {
        }).getBody();
    }

    /**
     * Adds a new vehicle.
     */
    public ResponseDTO<Void> addVehicle(Vehicle vehicle, HttpServletRequest request) {
        return restTemplate
                .post("/vehicles/save", vehicle, request, new ParameterizedTypeReference<ResponseDTO<Void>>() {
                }).getBody();
    }
    
    public ResponseDTO<Void> deleteVehicle(Long vehicleId, HttpServletRequest request) {
        return restTemplate
                .delete("/vehicles/delete/"+vehicleId, null, request, new ParameterizedTypeReference<ResponseDTO<Void>>() {
                }).getBody();
    }

    public Vehicle getVehicleByUserId(Long userId, HttpServletRequest request){
        return restTemplate.get("/vehicles/user", request, new ParameterizedTypeReference<Vehicle>() {
        }).getBody();
    }
}
