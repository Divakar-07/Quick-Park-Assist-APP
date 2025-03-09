package com.qpa.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.qpa.entity.Spot;
import com.qpa.entity.SpotBookingInfo;
import com.qpa.entity.Vehicle;
import com.qpa.repository.SpotBookingInfoRepository;
import com.qpa.repository.SpotRepository;
import com.qpa.repository.VehicleRepository;

@Service
public class SpotBookingService {
    private final SpotBookingInfoRepository spotBookingInfoRepository;
    private final SpotRepository spotRepository;
    private final VehicleRepository vehicleRepository;
    
    public SpotBookingService(SpotBookingInfoRepository spotBookingInfoRepository, SpotRepository spotRepository, VehicleRepository vehicleRepository){
        this.spotBookingInfoRepository = spotBookingInfoRepository;
        this.spotRepository = spotRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public SpotBookingInfo bookSpot(SpotBookingInfo bookingInfo, Long spotId, Long vehicleId){
        Optional <Spot> spot = spotRepository.findById(spotId);
        if (!spot.isPresent()){
            throw new RuntimeException("Invalid spotId");
        }

        Optional <Vehicle> vehicle = vehicleRepository.findById(vehicleId);
        
        if (!vehicle.isPresent()){
            throw new RuntimeException("Invalid vehicleId");
        }

        bookingInfo.setVehicle(vehicle.get());
        bookingInfo.setSpot(spot.get());
        return spotBookingInfoRepository.save(bookingInfo);
    
}

}
