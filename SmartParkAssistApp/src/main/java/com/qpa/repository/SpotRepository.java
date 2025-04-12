package com.qpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.qpa.entity.PriceType;
import com.qpa.entity.Spot;
import com.qpa.entity.SpotStatus;
import com.qpa.entity.SpotType;
import com.qpa.entity.VehicleType;

@Repository
public interface SpotRepository extends JpaRepository<Spot, Long> {
	List<Spot> findByStatus(SpotStatus status);
	List<Spot> findBySpotType(SpotType spotType);
	List<Spot> findByHasEVCharging(boolean hasEVCharging);
	List<Spot> findByPriceType(PriceType priceType);
	List<Spot> findByLocation_City(String city);
	List<Spot> findByLocation_Area(String area);
	List<Spot> findByOwnerUserId(Long userId);

	@Query("SELECT s FROM Spot s WHERE s.location.city = :city")
	List<Spot> findByLocationFilters(String city);
	
	@Query("SELECT s FROM Spot s JOIN s.supportedVehicleTypes v WHERE s.location.city = :city AND v = :vehicleType AND s.status = 'AVAILABLE'")
    List<Spot> findAvailableSpotsByCityAndVehicleType(@Param("city") String city, @Param("vehicleType") VehicleType vehicleType);
	
	Optional<Spot> findById(Long spotId); // ✅ Correct return type
}
