package com.qpa.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;


import com.qpa.dto.SpotCreateDTO;
import com.qpa.dto.SpotResponseDTO;
import com.qpa.dto.SpotStatistics;
import com.qpa.dto.LocationDTO;
import com.qpa.dto.RatingRequestDTO;
import com.qpa.dto.ResponseDTO;
import com.qpa.entity.PriceType;
import com.qpa.entity.SpotStatus;
import com.qpa.entity.SpotType;
import com.qpa.entity.UserInfo;
import com.qpa.entity.UserType;
import com.qpa.entity.VehicleType;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.qpa.service.UserService;

@RequestMapping("/spots")
@Controller
public class SpotUIController {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private UserService userService;

    private final String BASE_URL = "http://localhost:7212/api";
    private static final String LAST_LOCATION_SESSION_KEY = "lastUsedLocation";

    // Home Page
    @GetMapping("/")
    public String landingPage(HttpServletRequest request) {

        // Check for auto-login via cookie
        UserInfo userInfo = userService.getUserDetails(request).getData();
        if (userInfo == null) {
            return "redirect:/auth/login";
        }

        return "redirect:/spots/home";
    }

    // Home Page
    @GetMapping("/home")
    public String homePage(HttpServletRequest request, Model model) {
        // Check for auto-login via cookie
        UserInfo user = userService.getUserDetails(request).getData();
        if (user == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("UserInfo", user);
        return "home";
    }

    @PostMapping("/create")
    public String createSpot(
            @ModelAttribute SpotCreateDTO spotCreateDTO,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model,
            HttpServletRequest request) {
        // Ensure UserInfo is logged in
        UserInfo currentUserInfo = userService.getUserDetails(request).getData();
        if (currentUserInfo == null) {
            return "redirect:/auth/login";
        }

        try {
            // Add UserInfoId to the spot creation request
            spotCreateDTO.setOwner(currentUserInfo);

            // Prepare the request with multipart form data
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("spot", spotCreateDTO);
            body.add("UserInfoId", currentUserInfo.getUserId());

            // Add image file if present
            if (imageFile != null && !imageFile.isEmpty()) {
                body.add("image", imageFile.getResource());
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            String cookie = request.getHeader(HttpHeaders.COOKIE);
            if (cookie != null) {
                headers.add(HttpHeaders.COOKIE, cookie);
            }

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<SpotResponseDTO> response = restTemplate.postForEntity(
                    BASE_URL + "/spots/create",
                    requestEntity,
                    SpotResponseDTO.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                // Store the last used location in the session
                LocationDTO lastLocation = new LocationDTO();
                BeanUtils.copyProperties(spotCreateDTO.getLocation(), lastLocation);
                request.getSession().setAttribute(LAST_LOCATION_SESSION_KEY, lastLocation);

                return "redirect:/spots/search?spotCreationSuccess";
            } else {
                model.addAttribute("error", "Spot creation failed");
                return "spot_create";
            }
        } catch (HttpClientErrorException e) {
            String errorMessage = e.getResponseBodyAsString();
            model.addAttribute("error", "Spot creation failed: " + errorMessage);
            return "spot_create";
        } catch (Exception e) {
            model.addAttribute("error", "Unexpected error: " + e.getMessage());
            return "spot_create";
        }
    }

    // Show Spot Creation Page
    @GetMapping("/create")
    public String showCreateSpotPage(Model model, HttpServletRequest request) {
        // Ensure UserInfo is logged in
        UserInfo currentUserInfo = userService.getUserDetails(request).getData();
        if (currentUserInfo == null) {
            return "redirect:/auth/login";
        }

        if (currentUserInfo.getUserType() != UserType.ADMIN) {
            return "redirect:/home?error=unauthorized";
        }

        SpotCreateDTO spotCreateDTO = new SpotCreateDTO();

        // Retrieve last used location from session
        LocationDTO lastLocation = (LocationDTO) request.getSession().getAttribute(LAST_LOCATION_SESSION_KEY);
        model.addAttribute("lastUsedLocation", lastLocation);

        try {
            // Fetch states from backend
            ResponseEntity<List<String>> statesResponse = restTemplate.exchange(
                    BASE_URL + "/locations/states",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<String>>() {
                    });
            List<String> states = statesResponse.getBody();

            // Fetch all cities from backend (for spot creation)
            ResponseEntity<List<String>> citiesResponse = restTemplate.exchange(
                    BASE_URL + "/locations/cities",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<String>>() {
                    });
            List<String> cities = citiesResponse.getBody();

            // Fetch state-city map
            ResponseEntity<Map<String, List<String>>> stateCityMapResponse = restTemplate.exchange(
                    BASE_URL + "/locations/state-city-map",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, List<String>>>() {
                    });
            Map<String, List<String>> stateCityMap = stateCityMapResponse.getBody();

            // Fetch city-pincode map
            ResponseEntity<Map<String, String>> cityPincodeMapResponse = restTemplate.exchange(
                    BASE_URL + "/locations/city-pincode-map",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, String>>() {
                    });
            Map<String, String> cityPincodeMap = cityPincodeMapResponse.getBody();

            model.addAttribute("spotCreateDTO", spotCreateDTO);
            model.addAttribute("spotTypes", SpotType.values());
            model.addAttribute("priceTypes", PriceType.values());
            model.addAttribute("vehicleTypes", VehicleType.values());
            model.addAttribute("UserInfo", userService.getUserDetails(request).getData());

            // Add states, cities, and pincode mappings
            model.addAttribute("states", states);
            model.addAttribute("cities", cities);
            model.addAttribute("stateCityMap", stateCityMap);
            model.addAttribute("cityPincodeMap", cityPincodeMap);

        } catch (Exception e) {
            model.addAttribute("error", "Error fetching location data: " + e.getMessage());
        }

        return "spot_create";
    }

    // Optional: Method to clear last used location
    @GetMapping("/clear-last-location")
    public String clearLastLocation(HttpServletRequest request) {
        request.getSession().removeAttribute(LAST_LOCATION_SESSION_KEY);
        return "redirect:/spots/create";
    }

    @GetMapping("/api/cities-by-state")
    @ResponseBody
    public List<Map<String, String>> getCitiesByState(@RequestParam String state) {
        try {
            ResponseEntity<List<Map<String, String>>> response = restTemplate.exchange(
                    BASE_URL + "/locations/cities-by-state?state=" + state,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, String>>>() {
                    });
            return response.getBody();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @GetMapping("/search")
    public String combinedSpotsView(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) SpotType spotType,
            @RequestParam(required = false) Boolean hasEVCharging,
            @RequestParam(required = false) VehicleType supportedVehicleType,
            @RequestParam(required = false) SpotStatus status,
            Model model,
            HttpServletRequest request) {
        UserInfo currentUserInfo = userService.getUserDetails(request).getData();
        if (currentUserInfo == null) {
            return "redirect:/auth/login";
        }

        // Fetch cities with existing spots
        try {
            ResponseEntity<SpotResponseDTO[]> spotsResponse = restTemplate.exchange(
                    BASE_URL + "/spots/all",
                    HttpMethod.GET,
                    null,
                    SpotResponseDTO[].class);
            SpotResponseDTO[] allSpots = spotsResponse.getBody();

            // Extract unique cities with spots
            Set<String> citiesWithSpots = Arrays.stream(allSpots)
                    .map(spot -> spot.getLocation().getCity())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // Sort the cities with spots
            List<String> sortedCitiesWithSpots = new ArrayList<>(citiesWithSpots);
            Collections.sort(sortedCitiesWithSpots);

            // Rest of the existing search logic remains the same
            boolean hasFilters = city != null || spotType != null || hasEVCharging != null ||
                    supportedVehicleType != null || status != null;

            SpotResponseDTO[] spots;

            if (hasFilters) {
                // If filters applied, use search endpoint
                MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
                if (city != null && !city.isEmpty())
                    params.add("city", city);
                if (spotType != null)
                    params.add("spotType", spotType.toString());
                if (hasEVCharging != null)
                    params.add("hasEVCharging", hasEVCharging.toString());
                if (supportedVehicleType != null)
                    params.add("supportedVehicleType", supportedVehicleType.toString());
                if (status != null)
                    params.add("status", status.toString());

                String queryParams = params
                        .entrySet()
                        .stream()
                        .map(entry -> entry.getKey() + "=" + entry.getValue().get(0))
                        .collect(Collectors.joining("&"));

                String urlWithParams = BASE_URL + "/spots/search?" + queryParams;

                try {
                    ResponseEntity<SpotResponseDTO[]> response = restTemplate.exchange(
                            urlWithParams,
                            HttpMethod.GET,
                            null,
                            SpotResponseDTO[].class);
                    spots = response.getBody();
                } catch (Exception e) {
                    spots = new SpotResponseDTO[0]; // Empty array on error
                    model.addAttribute("error", "Error fetching filtered spots: " + e.getMessage());
                }
            } else {
                // If no filters, get all spots
                try {
                    spots = restTemplate.getForObject(BASE_URL + "/spots/all", SpotResponseDTO[].class);
                } catch (Exception e) {
                    spots = new SpotResponseDTO[0]; // Empty array on error
                    model.addAttribute("error", "Error fetching all spots: " + e.getMessage());
                }
            }

            // Add all necessary data to the model
            model.addAttribute("spots", spots);
            model.addAttribute("spotTypes", SpotType.values());
            model.addAttribute("vehicleTypes", VehicleType.values());
            model.addAttribute("status", SpotStatus.values());
            model.addAttribute("cities", sortedCitiesWithSpots); // Now uses only cities with spots
            UserInfo user = userService.getUserDetails(request).getData();
            model.addAttribute("user", user);

        } catch (Exception e) {
            model.addAttribute("error", "Error fetching spots: " + e.getMessage());
            model.addAttribute("cities", Collections.emptyList());
        }
        return "search_spots";
    }

    @GetMapping("/statistics")
    public String getSpotStatistics(Model model, HttpServletRequest request) {
        UserInfo currentUserInfo = userService.getUserDetails(request).getData();
        if (currentUserInfo == null) {
            return "redirect:/auth/login";
        }

        SpotStatistics statistics = restTemplate.getForObject(
                BASE_URL + "/spots/statistics",
                SpotStatistics.class);

        model.addAttribute("statistics", statistics);
        model.addAttribute("UserInfo", userService.getUserDetails(request).getData());
        return "statistics";
    }

    // Owner's Spots Page
    @GetMapping("/owner")
    public String getOwnerSpots(Model model, HttpServletRequest request) {
        UserInfo currentUserInfo = userService.getUserDetails(request).getData();
        if (currentUserInfo == null) {
            return "redirect:/auth/login";
        }

        SpotResponseDTO[] spots = restTemplate.getForObject(
                BASE_URL + "/spots/owner?userId=" + currentUserInfo.getUserId(),
                SpotResponseDTO[].class);

        model.addAttribute("spots", spots);
        model.addAttribute("UserInfo", userService.getUserDetails(request).getData());
        return "owner_spots";
    }

    // Show Edit Spot Form
    @GetMapping("/edit/{spotId}")
    public String showEditSpotForm(@PathVariable Long spotId, Model model, HttpServletRequest request) {
        // Ensure UserInfo is logged in
        UserInfo currentUserInfo = userService.getUserDetails(request).getData();
        if (currentUserInfo == null) {
            return "redirect:/auth/login";
        }

        try {
            // Fetch the spot details from the backend
            ResponseEntity<SpotResponseDTO> response = restTemplate.getForEntity(
                    BASE_URL + "/spots/" + spotId,
                    SpotResponseDTO.class);
            ResponseEntity<List<String>> citiesResponse = restTemplate.exchange(
                    BASE_URL + "/locations/cities",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<String>>() {
                    });
            List<String> cities = citiesResponse.getBody();

            if (response.getStatusCode().is2xxSuccessful()) {
                SpotResponseDTO spot = response.getBody();
                // Check if the current UserInfo is the owner of this spot
                if (spot == null || spot.getOwner() == null
                        || !spot.getOwner().getUserId().equals(currentUserInfo.getUserId())) {
                    return "redirect:/spots/owner?error=unauthorized";
                }

                model.addAttribute("spot", spot);
                model.addAttribute("currentUserInfo", currentUserInfo);
                model.addAttribute("spotTypes", SpotType.values());
                model.addAttribute("priceTypes", PriceType.values());
                model.addAttribute("vehicleTypes", VehicleType.values());
                model.addAttribute("cities", cities);
                model.addAttribute("status", SpotStatus.values());
                model.addAttribute("userType", userService.getUserDetails(request).getData().getUserType());
                System.out.println(userService.getUserDetails(request).getData().getUserType());
                return "update_spot";
            } else {
                return "redirect:/spots/owner?error=spotNotFound";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error retrieving spot: " + e.getMessage());
            return "redirect:/spots/owner?error=retrievalError";
        }
    }

    // Process Spot Update
    @PostMapping("/update")
    public String updateSpot(
            @RequestParam Long spotId,
            @ModelAttribute SpotCreateDTO spotCreateDTO,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam Long UserInfoId,
            Model model,
            HttpServletRequest request) {

        UserInfo currentUserInfo = userService.getUserDetails(request).getData();
        if (currentUserInfo == null) {
            return "redirect:/auth/login";
        }

        if (!UserInfoId.equals(currentUserInfo.getUserId())) {
            return "redirect:/spots/owner?error=unauthorized";
        }

        try {

            ResponseEntity<SpotResponseDTO> currentSpotResponse = restTemplate.getForEntity(
                    BASE_URL + "/spots/" + spotId,
                    SpotResponseDTO.class);

            if (currentSpotResponse.getStatusCode().is2xxSuccessful()) {
                SpotResponseDTO currentSpot = currentSpotResponse.getBody();

                if (spotCreateDTO.getSpotType() == null) {
                    spotCreateDTO.setSpotType(currentSpot.getSpotType());
                }

                if (spotCreateDTO.getLocation() == null) {
                    spotCreateDTO.setLocation(currentSpot.getLocation());
                } else {
                    if (spotCreateDTO.getLocation().getCity() == null) {
                        spotCreateDTO.getLocation().setCity(currentSpot.getLocation().getCity());
                    }
                    if (spotCreateDTO.getLocation().getState() == null) {
                        spotCreateDTO.getLocation().setState(currentSpot.getLocation().getState());
                    }
                    if (spotCreateDTO.getLocation().getPincode() == null) {
                        spotCreateDTO.getLocation().setPincode(currentSpot.getLocation().getPincode());
                    }
                    if (spotCreateDTO.getLocation().getLandmark() == null) {
                        spotCreateDTO.getLocation().setLandmark(currentSpot.getLocation().getLandmark());
                    }
                    if (spotCreateDTO.getSupportedVehicle() == null) {
                        spotCreateDTO.setSupportedVehicle(currentSpot.getSupportedVehicleTypes());
                    }
                }
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            String cookie = request.getHeader(HttpHeaders.COOKIE);
            if (cookie != null) {
                headers.add(HttpHeaders.COOKIE, cookie);
            }
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("spot", spotCreateDTO);
            body.add("UserInfoId", UserInfoId);
            body.add("spotId", spotId);

            if (imageFile != null && !imageFile.isEmpty()) {
                body.add("image", imageFile.getResource());
            }

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<SpotResponseDTO> response = restTemplate.exchange(
                    BASE_URL + "/spots/" + spotId,
                    HttpMethod.PUT,
                    requestEntity,
                    SpotResponseDTO.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return "redirect:/spots/owner?success=spotUpdated";
            } else {
                model.addAttribute("error", "Failed to update spot");
                return "redirect:/spots/edit/" + spotId + "?error=updateFailed";
            }
        } catch (HttpClientErrorException e) {
            String errorMessage = e.getResponseBodyAsString();
            model.addAttribute("error", "Update failed: " + errorMessage);
            return "redirect:/spots/edit/" + spotId + "?error=" + errorMessage;
        } catch (Exception e) {
            model.addAttribute("error", "Unexpected error: " + e.getMessage());
            return "redirect:/spots/edit/" + spotId + "?error=" + e.getMessage();
        }
    }

    // Process Spot Delete
    @GetMapping("/delete/{spotId}")
    public String deleteSpot(
            @PathVariable Long spotId,
            HttpServletRequest request,
            Model model) {
        // Ensure UserInfo is logged in
        UserInfo currentUserInfo = userService.getUserDetails(request).getData();
        if (currentUserInfo == null) {
            return "redirect:/auth/login";
        }

        try {
            // Get the spot to verify ownership
            ResponseEntity<SpotResponseDTO> getResponse = restTemplate.getForEntity(
                    BASE_URL + "/spots/" + spotId,
                    SpotResponseDTO.class);

            if (getResponse.getStatusCode().is2xxSuccessful()) {
                SpotResponseDTO spot = getResponse.getBody();

                // Check if the current UserInfo is the owner
                if (spot == null || spot.getOwner() == null
                        || !spot.getOwner().getUserId().equals(currentUserInfo.getUserId())) {
                    return "redirect:/spots/owner?error=unauthorizedDelete";
                }

                // Make delete request to backend with UserInfoId as parameter
                String deleteUrl = BASE_URL + "/spots/" + spotId + "?UserInfoId=" + currentUserInfo.getUserId();
                restTemplate.delete(deleteUrl);

                return "redirect:/spots/owner?success=spotDeleted";
            } else {
                return "redirect:/spots/owner?error=spotNotFound";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error deleting spot: " + e.getMessage());
            return "redirect:/spots/owner?error=deletionError";
        }
    }

    @GetMapping(value = "/toggle")
    public String toggleSpotActivation(

            @RequestParam Long spotId,

            HttpServletRequest request

    ) {
        // Ensure UserInfo is logged in
        UserInfo currentUserInfo = userService.getUserDetails(request).getData();
        if (currentUserInfo == null) {
            return "redirect:/auth/login";
        }

        try {
            // Call the backend API to toggle spot activation
            ResponseEntity<SpotResponseDTO> response = restTemplate.exchange(

                    BASE_URL + "/spots/toggle/" + spotId,

                    HttpMethod.PUT,

                    null,

                    SpotResponseDTO.class

            );
            if (response.getStatusCode().is2xxSuccessful()) {
                return "redirect:/spots/owner?success=statusToggled";
            } else {
                return "redirect:/spots/owner?error=toggleFailed";
            }
        } catch (Exception e) {
            return "redirect:/spots/owner?error=" + e.getMessage();
        }
    }

    @GetMapping("/api/booked-cities")
    @ResponseBody
    public List<String> getBookedCities() {
        try {
            ResponseEntity<SpotResponseDTO[]> response = restTemplate.exchange(
                    BASE_URL + "/spots/booked",
                    HttpMethod.GET,
                    null,
                    SpotResponseDTO[].class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Extract unique cities with booked spots
                Set<String> citiesWithSpots = Arrays.stream(response.getBody())
                    .map(spot -> spot.getLocation().getCity())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
                
                // Sort the cities
                List<String> sortedCities = new ArrayList<>(citiesWithSpots);
                Collections.sort(sortedCities);
                return sortedCities;
            }
            return Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @GetMapping("/api/booked-landmarks")
    @ResponseBody
    public List<String> getBookedLandmarks(@RequestParam String city) {
        try {
            ResponseEntity<SpotResponseDTO[]> response = restTemplate.exchange(
                    BASE_URL + "/spots/booked",
                    HttpMethod.GET,
                    null,
                    SpotResponseDTO[].class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Extract unique landmarks in the selected city with booked spots
                Set<String> landmarksInCity = Arrays.stream(response.getBody())
                    .filter(spot -> spot.getLocation().getCity() != null && 
                            spot.getLocation().getCity().equals(city))
                    .map(spot -> spot.getLocation().getLandmark())
                    .filter(Objects::nonNull)
                    .filter(landmark -> !landmark.isEmpty())
                    .collect(Collectors.toSet());
                
                // Sort the landmarks
                List<String> sortedLandmarks = new ArrayList<>(landmarksInCity);
                Collections.sort(sortedLandmarks);
                return sortedLandmarks;
            }
            return Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @GetMapping("/booked")
    public String getBookedSpots(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String landmark,
            Model model, 
            HttpServletRequest request) {
        
        UserInfo currentUserInfo = userService.getUserDetails(request).getData();
        if (currentUserInfo == null) {
            return "redirect:/auth/login";
        }

        try {
            // Get all booked spots
            ResponseEntity<SpotResponseDTO[]> allSpotsResponse = restTemplate.exchange(
                    BASE_URL + "/spots/booked",
                    HttpMethod.GET,
                    null,
                    SpotResponseDTO[].class);
            
            SpotResponseDTO[] allBookedSpots = allSpotsResponse.getBody();
            
            // Extract unique cities with booked spots
            Set<String> citiesWithSpots = Arrays.stream(allBookedSpots)
                .map(spot -> spot.getLocation().getCity())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            
            // Sort the cities
            List<String> sortedCities = new ArrayList<>(citiesWithSpots);
            Collections.sort(sortedCities);
            
            // Extract landmarks for the selected city
            List<String> landmarks = Collections.emptyList();
            if (city != null && !city.isEmpty()) {
                Set<String> landmarksInCity = Arrays.stream(allBookedSpots)
                    .filter(spot -> spot.getLocation().getCity() != null && 
                            spot.getLocation().getCity().equals(city))
                    .map(spot -> spot.getLocation().getLandmark())
                    .filter(Objects::nonNull)
                    .filter(ctLandmark -> !ctLandmark.isEmpty())
                    .collect(Collectors.toSet());
                
                landmarks = new ArrayList<>(landmarksInCity);
                Collections.sort(landmarks);
            }
            
            // Filter spots based on city and landmark if needed
            SpotResponseDTO[] filteredSpots;
            
            if (city != null && !city.isEmpty()) {
                if (landmark != null && !landmark.isEmpty()) {
                    // Filter by both city and landmark
                    filteredSpots = Arrays.stream(allBookedSpots)
                        .filter(spot -> city.equals(spot.getLocation().getCity()) && 
                                landmark.equals(spot.getLocation().getLandmark()))
                        .toArray(SpotResponseDTO[]::new);
                } else {
                    // Filter by city only
                    filteredSpots = Arrays.stream(allBookedSpots)
                        .filter(spot -> city.equals(spot.getLocation().getCity()))
                        .toArray(SpotResponseDTO[]::new);
                }
            } else {
                // No filtering
                filteredSpots = allBookedSpots;
            }
            
            model.addAttribute("spots", filteredSpots);
            model.addAttribute("cities", sortedCities);
            model.addAttribute("landmarks", landmarks);
            model.addAttribute("UserInfo", currentUserInfo);
            
        } catch (HttpClientErrorException.NotFound ex) {
            model.addAttribute("spots", new SpotResponseDTO[0]);
            model.addAttribute("cities", Collections.emptyList());
            model.addAttribute("landmarks", Collections.emptyList());
            model.addAttribute("message", "No booked spots found.");
        } catch (Exception e) {
            model.addAttribute("error", "Error fetching booked spots: " + e.getMessage());
            model.addAttribute("spots", new SpotResponseDTO[0]);
            model.addAttribute("cities", Collections.emptyList());
            model.addAttribute("landmarks", Collections.emptyList());
        }

        return "booked_spots_list";
    }

    @GetMapping("/by-booking")
    public String getSpotByBookingIdPage(@RequestParam(required = false) Long bookingId, Model model,
            HttpServletRequest request) {
        UserInfo currentUserInfo = userService.getUserDetails(request).getData();
        if (currentUserInfo == null) {
            return "redirect:/auth/login";
        }
        if (bookingId == null) {
            return "search_spot_bookingId";
        }
        try {
            String url = BASE_URL + "/spots/by-booking/" + bookingId;
            ResponseEntity<SpotResponseDTO> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, SpotResponseDTO.class);
            SpotResponseDTO spot = response.getBody();
            model.addAttribute("spot", spot);
            
        } catch (HttpClientErrorException.NotFound ex) {
            // This will catch 404 errors specifically
            model.addAttribute("errorMessage", "No spot found for booking ID: " + bookingId);
        } catch (Exception e) {
            // Changed to show invalid ID message for all other errors
            model.addAttribute("errorMessage", "Invalid booking ID: " + bookingId);
        }
        return "search_spot_bookingId";
    }

    @GetMapping("/search-by-date")
    public String searchSpotsByDate(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Model model,
            HttpServletRequest request) {
        UserInfo currentUserInfo = userService.getUserDetails(request).getData();
        if (currentUserInfo == null) {
            return "redirect:/auth/login";
        }

        boolean hasDateFilters = startDate != null && !startDate.isEmpty()
                && endDate != null && !endDate.isEmpty();

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("UserInfo", userService.getUserDetails(request).getData());

        List<SpotResponseDTO> spots = new ArrayList<>();

        if (hasDateFilters) {
            try {
                StringBuilder urlBuilder = new StringBuilder(
                        BASE_URL + "/spots/by-booking?startDate=" + startDate + "&endDate=" + endDate);

                String url = urlBuilder.toString();

                ResponseEntity<SpotResponseDTO[]> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        SpotResponseDTO[].class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    spots = Arrays.asList(response.getBody());
                    model.addAttribute("spotsFound", spots.size() > 0);
                }
            } catch (Exception e) {
                model.addAttribute("error", "Error searching for spots: " + e.getMessage());
                model.addAttribute("spotsFound", false);
            }
        } else {
            model.addAttribute("spotsFound", null);
        }

        model.addAttribute("spots", spots);

        return "search_spots_by_date";
    }

    // New endpoint to view single spot details
    @GetMapping("/details/{spotId}")
    public String viewSpotDetails(@PathVariable Long spotId, Model model, HttpServletRequest request) {
        // Ensure user is logged in
        UserInfo currentUser = userService.getUserDetails(request).getData();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        try {
            // Fetch the spot details from the backend
            ResponseEntity<SpotResponseDTO> response = restTemplate.getForEntity(
                    BASE_URL + "/spots/" + spotId,
                    SpotResponseDTO.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                SpotResponseDTO spot = response.getBody();
                model.addAttribute("spot", spot);
                model.addAttribute("UserInfo", userService.getUserDetails(request).getData());
                return "spot_details";
            } else {
                return "redirect:/spots/search?error=spotNotFound";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error retrieving spot: " + e.getMessage());
            return "redirect:/spots/search?error=retrievalError";
        }
    }

    // API endpoint to get user's rating for a spot
    @GetMapping("/api/ratings/{spotId}/user-rating")
    @ResponseBody
    public ResponseDTO<Integer> getUserRatingForSpot(@PathVariable Long spotId, HttpServletRequest request) {
        UserInfo currentUser = userService.getUserDetails(request).getData();
        if (currentUser == null) {
            return new ResponseDTO<>("Unauthorized", 401, false);
        }

        try {
            // Forward the request to the backend API
            HttpHeaders headers = new HttpHeaders();
            String cookie = request.getHeader(HttpHeaders.COOKIE);
            if (cookie != null) {
                headers.add(HttpHeaders.COOKIE, cookie);
            }

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<ResponseDTO<Integer>> response = restTemplate.exchange(
                    BASE_URL + "/ratings/" + spotId + "/user",
                    HttpMethod.GET,
                    requestEntity,
                    new ParameterizedTypeReference<ResponseDTO<Integer>>() {
                    });

            return response.getBody();
        } catch (HttpClientErrorException.NotFound ex) {
            return new ResponseDTO<>("No rating found", 404, false);
        } catch (Exception e) {
            return new ResponseDTO<>("Error: " + e.getMessage(), 500, false);
        }
    }

    // API endpoint to submit a rating
    @PostMapping("/api/ratings/{spotId}/submit")
    @ResponseBody
    public ResponseDTO<?> submitRating(@PathVariable Long spotId, @RequestBody RatingRequestDTO ratingRequest,
            HttpServletRequest request) {
        UserInfo currentUser = userService.getUserDetails(request).getData();
        if (currentUser == null) {
            return new ResponseDTO<>("Unauthorized", 401, false);
        }

        try {
            // Forward the request to the backend API
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String cookie = request.getHeader(HttpHeaders.COOKIE);
            if (cookie != null) {
                headers.add(HttpHeaders.COOKIE, cookie);
            }

            HttpEntity<RatingRequestDTO> requestEntity = new HttpEntity<>(ratingRequest, headers);
            ResponseEntity<ResponseDTO<?>> response = restTemplate.exchange(
                    BASE_URL + "/ratings/" + spotId,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<ResponseDTO<?>>() {
                    });

            return response.getBody();
        } catch (Exception e) {
            return new ResponseDTO<>("Error submitting rating: " + e.getMessage(), 500, false);
        }
    }

    @GetMapping("/spots/booked")
    public String bookedSpotsByCityAndLandmark(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String landmark,
            Model model,
            HttpServletRequest request) {

        UserInfo currentUser = (UserInfo) request.getSession().getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Fetch cities with existing spots
        try {

            System.out.println("==== About to call REST API for cities ====");
            String cityUrl = BASE_URL + "/spots/booked/cities";
            ResponseEntity<List> cityResponse = restTemplate.exchange(
                    cityUrl, HttpMethod.GET, null, List.class);

            System.out.println("==== REST API call completed with status: " + cityResponse.getStatusCode() + " ====");
            List<String> cities = cityResponse.getBody();
            System.out.println("==== Cities received from API: " + cities + " ====");

            // To check what is being fetched
            System.out.println("Cities fetched: " + cities);

            // Sort the cities with spots
            if (cities != null) {
                Collections.sort(cities);
            } else {
                cities = new ArrayList<>(); // Initialize if null
            }

            // Rest of the existing search logic remains the same
            boolean hasFilters = city != null || landmark != null;

            SpotResponseDTO[] spots;

            if (hasFilters) {
                // If filters applied, use search endpoint
                MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
                if (city != null && !city.isEmpty())
                    params.add("city", city);
                if (landmark != null && !landmark.isEmpty())
                    params.add("landmark", landmark);

                String queryParams = params
                        .entrySet()
                        .stream()
                        .map(entry -> entry.getKey() + "=" + entry.getValue().get(0))
                        .collect(Collectors.joining("&"));

                String urlWithParams = BASE_URL + "/spots/booked-by-filters?" + queryParams;

                try {
                    ResponseEntity<SpotResponseDTO[]> response = restTemplate.exchange(
                            urlWithParams,
                            HttpMethod.GET,
                            null,
                            SpotResponseDTO[].class);
                    spots = response.getBody();
                } catch (Exception e) {
                    spots = new SpotResponseDTO[0]; // Empty array on error
                    model.addAttribute("error", "Error fetching filtered spots: " + e.getMessage());
                }
            } else {
                // If no filters, get all spots
                try {
                    spots = restTemplate.getForObject(BASE_URL + "/spots/booked", SpotResponseDTO[].class);
                } catch (Exception e) {
                    spots = new SpotResponseDTO[0]; // Empty array on error
                    model.addAttribute("error", "Error fetching all spots: " + e.getMessage());
                }
            }

            // Add all necessary data to the model
            model.addAttribute("spots", spots);
            model.addAttribute("cities", cities);

        } catch (Exception e) {
            System.out.println("==== Exception occurred while calling REST API: " + e.getMessage() + " ====");
            model.addAttribute("error", "Error fetching spots: " + e.getMessage());
            model.addAttribute("cities", Collections.emptyList());
        }

        return "booked_spots_list";
    }
}
