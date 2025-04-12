package com.qpa.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.qpa.dto.AdminSpotsStatistics;
import com.qpa.dto.LocationDTO;
import com.qpa.dto.SpotCreateDTO;
import com.qpa.dto.SpotResponseDTO;
import com.qpa.dto.SpotSearchCriteria;
import com.qpa.dto.SpotStatistics;
import com.qpa.entity.Location;
import com.qpa.entity.Payment;
import com.qpa.entity.PriceType;
import com.qpa.entity.Spot;
import com.qpa.entity.SpotBookingInfo;
import com.qpa.entity.SpotStatus;
import com.qpa.entity.VehicleType;
import com.qpa.exception.InvalidEntityException;
import com.qpa.exception.ResourceNotFoundException;
import com.qpa.exception.UnauthorizedAccessException;
import com.qpa.repository.LocationRepository;
import com.qpa.repository.SpotBookingInfoRepository;
import com.qpa.repository.SpotRepository;
import com.qpa.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class SpotService {

	private final SpotBookingService spotBookingService;
	private final SpotRepository spotRepository;
	private final UserRepository userRepository;
	private final SpotBookingInfoRepository bookingRepository;
	private final AuthService authService;
	private final LocationService locationService;
	private final CloudinaryService cloudinaryService;
	private final PaymentService paymentService;

	@Autowired
	public SpotService(SpotRepository spotRepository, LocationRepository locationRepository,
			UserRepository userRepository, SpotBookingInfoRepository bookingRepository, AuthService authService,
			LocationService locationService, CloudinaryService cloudinaryService,
			SpotBookingService spotBookingService, PaymentService paymentService) {
		this.spotRepository = spotRepository;
		this.userRepository = userRepository;
		this.bookingRepository = bookingRepository;
		this.authService = authService;
		this.locationService = locationService;
		this.cloudinaryService = cloudinaryService;
		this.spotBookingService = spotBookingService;
		this.paymentService = paymentService;
	}

	public SpotResponseDTO createSpot(SpotCreateDTO spotDTO, MultipartFile spotImage, Long userId,
			HttpServletRequest request) throws IOException {
		if (!authService.isAuthenticated(request)) {
			throw new UnauthorizedAccessException("USER IS NOT AUTHENTICATED");
		}
		Spot spot = new Spot();
		spot.setSpotNumber(spotDTO.getSpotNumber());
		spot.setSpotType(spotDTO.getSpotType());
		spot.setStatus(SpotStatus.AVAILABLE);
		spot.setHasEVCharging(spotDTO.getHasEVCharging());
		spot.setPrice(spotDTO.getPrice());
		spot.setPriceType(spotDTO.getPriceType());
		spot.setSupportedVehicleTypes(spotDTO.getSupportedVehicle());
		spot.setOwner(userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId)));

		// Use LocationService to find or create location
		Location location = locationService.findOrCreateLocation(spotDTO.getLocation());
		spot.setLocation(location);

		spot = spotRepository.save(spot);

		if (spotImage != null && !spotImage.isEmpty()) {
			String imageUrl = cloudinaryService.uploadSpotImage(spotImage, spot.getSpotId(), null);
			spot.setSpotImage(imageUrl);
			spot = spotRepository.save(spot);
		}

		return convertToDTO(spot);
	}

	public SpotResponseDTO updateSpot(Long spotId, SpotCreateDTO spotDTO, MultipartFile spotImage)
			throws InvalidEntityException, IOException {
		Spot spot = spotRepository.findById(spotId)
				.orElseThrow(() -> new InvalidEntityException("Spot not found with id : " + spotId));

		// Update spot details
		spot.setSpotNumber(spotDTO.getSpotNumber());
		spot.setSpotType(spotDTO.getSpotType());
		spot.setHasEVCharging(spotDTO.getHasEVCharging());
		spot.setPrice(spotDTO.getPrice());
		spot.setPriceType(spotDTO.getPriceType());
		spot.setSupportedVehicleTypes(spotDTO.getSupportedVehicle());
		spot.setStatus(spotDTO.getStatus());

		// Use LocationService to find or create location
		Location location = locationService.findOrCreateLocation(spotDTO.getLocation());
		spot.setLocation(location);

		spot = spotRepository.save(spot);

		if (spotImage != null && !spotImage.isEmpty()) {
			String imageUrl = cloudinaryService.uploadSpotImage(spotImage, spot.getSpotId(), null);
			spot.setSpotImage(imageUrl);
			spot = spotRepository.save(spot);
		}

		return convertToDTO(spot);
	}

	public void deleteSpot(Long spotId, HttpServletRequest request) {
		if (!authService.isAuthenticated(request)) {
			throw new UnauthorizedAccessException("USER IS NOT AUTHENTICATED");
		}
		spotRepository.deleteById(spotId);
	}

	public SpotResponseDTO getSpot(Long spotId) {
		Spot spot = spotRepository.findById(spotId)
				.orElseThrow(() -> new ResourceNotFoundException("Spot not found with id : " + spotId));
		return convertToDTO(spot);
	}

	public List<SpotResponseDTO> getAllSpots() {
		return spotRepository.findAll().stream()
				.filter(spot -> spot.getStatus() == SpotStatus.AVAILABLE || spot.getStatus() == SpotStatus.UNAVAILABLE)
				.filter(Spot::getIsActive)
				.map(this::convertToDTO)
				.collect(Collectors.toList());
	}

	public List<SpotResponseDTO> getSpotByOwner(Long userId) {
		return spotRepository.findByOwnerUserId(userId).stream()
				.map(this::convertToDTO)
				.collect(Collectors.toList());
	}

	public List<SpotResponseDTO> searchSpots(SpotSearchCriteria criteria) {
		// Implementation for filtering spots based on various criteria
		List<Spot> spots = spotRepository.findByLocationFilters(
				criteria.getCity());

		return spots.stream()
				.filter(spot -> spot.getIsActive())
				.filter(spot -> spot.getStatus() == SpotStatus.AVAILABLE || spot.getStatus() == SpotStatus.UNAVAILABLE)
				.filter(spot -> criteria.getSpotType() == null || spot.getSpotType() == criteria.getSpotType())
				.filter(spot -> criteria.getHasEVCharging() == null
						|| spot.getHasEVCharging() == criteria.getHasEVCharging())
				.filter(spot -> criteria.getPriceType() == null || spot.getPriceType() == criteria.getPriceType())
				.filter(spot -> criteria.getSupportedVehicleType() == null ||
						spot.getSupportedVehicleTypes().contains(criteria.getSupportedVehicleType()))
				.filter(spot -> criteria.getStatus() == null || spot.getStatus() == criteria.getStatus())
				.map(this::convertToDTO)
				.collect(Collectors.toList());
	}

	public SpotResponseDTO rateSpot(Long spotId, Double rating, HttpServletRequest request) {
		if (!authService.isAuthenticated(request)) {
			throw new UnauthorizedAccessException("USER IS NOT AUTHENTICATED");
		}
		Spot spot = spotRepository.findById(spotId)
				.orElseThrow(() -> new ResourceNotFoundException("Spot not found with id : " + spotId));

		spot.setAverageRating(rating);
		spot = spotRepository.save(spot);

		return convertToDTO(spot);
	}

	public SpotStatistics getStatistics() {
		List<Spot> allSpots = spotRepository.findAll();

		// Calculate total spots
		long totalSpots = allSpots.size();

		// Calculate available and unavailable spots
		long availableSpots = allSpots.stream()
				.filter(spot -> spot.getStatus() == SpotStatus.AVAILABLE)
				.count();
		long unavailableSpots = totalSpots - availableSpots;

		return new SpotStatistics(
				totalSpots,
				availableSpots,
				unavailableSpots);
	}

	private SpotResponseDTO convertToDTO(Spot spot) {
		SpotResponseDTO dto = new SpotResponseDTO();
		BeanUtils.copyProperties(spot, dto);

		if (spot.getLocation() != null) {
			LocationDTO locationDTO = new LocationDTO();
			BeanUtils.copyProperties(spot.getLocation(), locationDTO);
			dto.setLocation(locationDTO);
		}

		return dto;
	}

	public List<SpotResponseDTO> getSpotsByEVCharging(boolean hasEVCharging) {
		List<Spot> spots = spotRepository.findByHasEVCharging(hasEVCharging);
		return spots.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	public List<SpotResponseDTO> getAvailableSpotsByCityAndVehicle(String city, VehicleType vehicleType) {
		List<Spot> spots = spotRepository.findAvailableSpotsByCityAndVehicleType(city, vehicleType);
		return spots.stream()
				.map(this::convertToDTO)
				.collect(Collectors.toList());
	}

	public List<SpotResponseDTO> getAvailableSpots() {
		List<Spot> spots = spotRepository.findByStatus(SpotStatus.AVAILABLE);
		return spots.stream()
				.map(this::convertToDTO)
				.collect(Collectors.toList());
	}

	public SpotResponseDTO getSpotByBookingId(long bookingId) throws InvalidEntityException {
		Spot spot = bookingRepository.findSpotByBookingId(bookingId);
		if (spot == null) {
			throw new InvalidEntityException("No spot found for booking ID: " + bookingId);
		}
		return convertToDTO(spot);
	}

	public List<SpotResponseDTO> getBookedSpots() {
		List<Spot> bookedSpots = bookingRepository.findBookedSpots();
		if (bookedSpots.isEmpty()) {
			throw new ResourceNotFoundException("No booked spots found.");
		}
		return bookedSpots.stream()
				.map(this::convertToDTO)
				.collect(Collectors.toList());
	}

	public List<SpotResponseDTO> getAvailableSpotsByStartAndEndDate(LocalDate startDate, LocalDate endDate) {
		List<Spot> bookedSpots = bookingRepository.findSpotsByStartAndEndDate(startDate, endDate);
		if (bookedSpots.isEmpty()) {
			throw new ResourceNotFoundException("No booked spots found between " + startDate + " and " + endDate);
		}
		return bookedSpots.stream()
				.filter(spot -> spot.getStatus() == SpotStatus.AVAILABLE)
				.map(this::convertToDTO)
				.collect(Collectors.toList());
	}

	public SpotResponseDTO toggleSpotActivation(Long spotId) {
		Spot spot = spotRepository.findById(spotId)
				.orElseThrow(() -> new ResourceNotFoundException("Spot not found with id : " + spotId));
		spot.setIsActive(!spot.getIsActive());
		spot = spotRepository.save(spot);
		return convertToDTO(spot);
	}

	public Double getTotalAmount(Long spotId, Double bookingHours) {
		Spot spot = spotRepository.findById(spotId)
				.orElseThrow(() -> new ResourceNotFoundException("Spot not found with id : " + spotId));

		double price = spot.getPrice();

		if (spot.getPriceType() == PriceType.DAILY) {
			long days = (long) Math.ceil(bookingHours / 24); // Round up to full days
			return price * days;
		} else {
			return price * bookingHours;
		}
	}

	// find booked spots based on city and landmark
	public List<SpotResponseDTO> getBookedSpots(String city, String landmark) {
		List<Spot> spots = bookingRepository.findByCityAndLandmark(city, landmark);

		if (spots.isEmpty()) {
			throw new ResourceNotFoundException("No booked spots for given criteria");
		}

		return spots.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	// to populate cities dropdown in frontend
	public List<String> getCities() {
		System.out.println("==== spotService.getCities() called ====");
		List<Spot> spots = bookingRepository.findBookedSpots();
		System.out.println("==== Repository returned " + (spots != null ? spots.size() : 0) + " spots ====");

		// Debug log to check what spots are being returned
		System.out.println("Found spots count: " + (spots != null ? spots.size() : 0));

		if (spots == null || spots.isEmpty()) {
			return new ArrayList<>(); // Return empty list rather than null
		}

		List<String> cities = spots.stream()
				.map(spot -> spot.getLocation().getCity())
				.filter(city -> city != null && !city.isEmpty()) // Filter out null/empty cities
				.distinct()
				.collect(Collectors.toList());

		System.out.println("==== Final distinct cities list: " + cities + " ====");

		return cities;
	}

	// to populate dropdown for Landmark in frontend
	public List<String> getLandmarks(String city) {
		System.out.println("==== spotService.getLandmarks(city) called ====");
		List<Spot> spots = bookingRepository.findByCityAndLandmark(city, null);
		System.out.println("==== Repository returned " + (spots != null ? spots.size() : 0) + " spots ====");

		if (spots == null || spots.isEmpty()) {
			return new ArrayList<>(); // Return empty list rather than null
		}
		List<String> landmarks = spots.stream()
				.map(Spot -> Spot.getLocation().getLandmark())
				.filter(Objects::nonNull)
				.distinct()
				.collect(Collectors.toList());

		System.out.println("==== Final distinct landmarks list: " + landmarks + " ====");

		return landmarks;
	}

	public AdminSpotsStatistics getAdminSpotsStatistics(Long userId) throws InvalidEntityException {
		AdminSpotsStatistics statistics = new AdminSpotsStatistics();
		List<Spot> adminSpots = spotRepository.findByOwnerUserId(userId);

		List<SpotBookingInfo> allBookings = new ArrayList<>();
		List<Spot> inActiveSpots = adminSpots.stream()
				.filter(spot -> spot.getStatus() != SpotStatus.AVAILABLE)
				.collect(Collectors.toList());

		List<Payment> payments = paymentService.getAllPaymentsByAdmin(userId);

		for (Spot spot : adminSpots) {
			try {
				allBookings.addAll(spotBookingService.getBookingsBySlotId(spot.getSpotId()));
			} catch (InvalidEntityException e) {
			}
		}

		statistics.setTotalSpots(adminSpots.size());
		statistics.setTotalBookings(allBookings.size());
		statistics.setActiveSpots(adminSpots.size() - inActiveSpots.size());
		statistics.setInactiveSpots(inActiveSpots.size());

		List<SpotBookingInfo> activeBookings = allBookings.stream().filter(booking -> {
			LocalDate today = LocalDate.now();
			LocalTime now = LocalTime.now();

			boolean isOngoingBooking = (booking.getStartDate().isBefore(today) ||
					(booking.getStartDate().isEqual(today) && booking.getStartTime().isBefore(now))) &&
					(booking.getEndDate().isAfter(today) ||
							(booking.getEndDate().isEqual(today) && booking.getEndTime().isAfter(now)));

			boolean isFutureBooking = booking.getStartDate().isAfter(today) ||
					(booking.getStartDate().isEqual(today) && booking.getStartTime().isAfter(now));

			return isOngoingBooking || isFutureBooking;
		})
				.collect(Collectors.toList());

		List<SpotBookingInfo> unpaidBookings = activeBookings.stream()
				.filter(booking -> payments.stream()
						.noneMatch(payment -> payment.getBookingId().equals(booking.getBookingId())))
				.collect(Collectors.toList());
		statistics.setPendingPayments(unpaidBookings.size());

		List<Spot> uniqueActiveSpots = adminSpots.stream()
				.filter(spot -> activeBookings.stream()
						.anyMatch(booking -> booking.getSpotInfo().getSpotId().equals(spot.getSpotId())))
				.distinct()
				.collect(Collectors.toList());

		statistics.setBookedSpots(uniqueActiveSpots.size());
		statistics.setActiveBookings(activeBookings.size());

		return statistics;

	}

}
