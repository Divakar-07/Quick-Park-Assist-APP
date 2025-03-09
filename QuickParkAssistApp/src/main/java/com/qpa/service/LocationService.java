package com.qpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qpa.entity.Location;
import com.qpa.repository.LocationRepository;

@Service
public class LocationService {
    @Autowired private LocationRepository locationRepository;

    public Location addLocation(Location location){
        return locationRepository.save(location);
    }
}
