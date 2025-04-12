package com.qpa.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.qpa.dto.LocationDTO;
import com.qpa.entity.City;
import com.qpa.entity.Location;
import com.qpa.entity.State;
import com.qpa.repository.CityRepository;
import com.qpa.repository.LocationRepository;
import com.qpa.repository.StateRepository;

import jakarta.transaction.Transactional;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;

    public LocationService(LocationRepository locationRepository, StateRepository stateRepository,
            CityRepository cityRepository) {
        this.locationRepository = locationRepository;
        this.stateRepository = stateRepository;
        this.cityRepository = cityRepository;
    }

    public Location findOrCreateLocation(LocationDTO locationDTO) {
        // Search for an existing location with the same unique attributes
        Optional<Location> existingLocation = locationRepository.findByUniqueLocationAttributes(
                locationDTO.getStreetAddress(),
                locationDTO.getCity(),
                locationDTO.getState(),
                locationDTO.getPincode(),
                locationDTO.getLandmark());

        // If location exists, return the existing one
        if (existingLocation.isPresent()) {
            return existingLocation.get();
        }

        // If no existing location, create a new one
        Location location = new Location();
        BeanUtils.copyProperties(locationDTO, location);
        return locationRepository.save(location);
    }

    @Transactional
    public void initializeLocations(Map<String, List<String>> stateCityMap, Map<String, String> cityPincodeMap) {
        // Populate states
        stateCityMap.keySet().forEach(stateName -> {
            if (!stateRepository.existsByName(stateName)) {
                State state = new State(stateName);
                stateRepository.save(state);
            }
        });

        // Populate cities
        for (Map.Entry<String, List<String>> entry : stateCityMap.entrySet()) {
            String stateName = entry.getKey();
            for (String cityName : entry.getValue()) {
                if (!cityRepository.existsByName(cityName)) {
                    String pincode = cityPincodeMap.getOrDefault(cityName, "");
                    City city = new City(cityName, pincode, stateName);
                    cityRepository.save(city);
                }
            }
        }
    }

    public List<String> getAllStates() {
        return cityRepository.findAllUniqueStates();
    }

    public List<String> getAllCities() {
        return cityRepository.findAllUniqueCities();
    }

    public Map<String, List<String>> getStateCityMap() {
        List<City> cities = cityRepository.findAll();
        return cities.stream()
                .collect(Collectors.groupingBy(
                        City::getStateName,
                        Collectors.mapping(City::getName, Collectors.toList())));
    }

    public Map<String, String> getCityPincodeMap() {
        List<City> cities = cityRepository.findAll();
        return cities.stream()
                .collect(Collectors.toMap(
                        City::getName,
                        City::getPincode,
                        (v1, v2) -> v1 // In case of duplicate keys, keep the first value
                ));
    }

    public List<City> getCitiesByState(String stateName) {
        return cityRepository.findByStateName(stateName);
    }
}
