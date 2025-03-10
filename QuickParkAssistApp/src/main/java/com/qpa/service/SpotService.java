package com.qpa.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qpa.entity.Location;
import com.qpa.entity.Spot;
import com.qpa.repository.LocationRepository;
import com.qpa.repository.SpotRepository;


@Service
public class SpotService {
    @Autowired private SpotRepository spotRepository;
    @Autowired private LocationRepository locationRepository;
    public Spot addSpot(Spot spot, Long locationId){
        Optional<Location> location = locationRepository.findById(locationId);
        
        if (!location.isPresent()){
            throw new RuntimeException("location with the requested locationId doesn't exist");
        }

        spot.setLocation(location.get());
        return spotRepository.save(spot);
    }

    public List<Spot> getAllSpots(){
        return spotRepository.findAll();
    }
}
