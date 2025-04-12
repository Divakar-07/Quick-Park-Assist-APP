package com.qpa.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.qpa.entity.Spot;
import com.qpa.entity.SpotBookingInfo;

@Repository
public interface SpotBookingInfoRepository extends JpaRepository<SpotBookingInfo, Long> {

        @Query("SELECT s.spotInfo FROM SpotBookingInfo s WHERE s.bookingId = :bookingId")
        Spot findSpotByBookingId(@Param("bookingId") long bookingId);

        @Query("SELECT s.spotInfo FROM SpotBookingInfo s WHERE s.status = 'CONFIRMED'")
        List<Spot> findBookedSpots();

        @Query("SELECT s.spotInfo FROM SpotBookingInfo s " +
                        "WHERE :startDate <= s.endDate AND :endDate >= s.startDate " +
                        "AND s.status = 'CONFIRMED'")
        List<Spot> findSpotsByStartAndEndDate(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        boolean existsBySpotInfo_SpotIdAndVehicle_RegistrationNumber(Long spotId, String registrationNumber);

        List<SpotBookingInfo> findByVehicle_UserObj_ContactNumber(String contactNumber);

        // Fetch all bookings for a specific vehicle
        List<SpotBookingInfo> findByVehicle_VehicleId(long vehicleId);

        // Fetch all bookings for a specific spot (Ensuring consistent naming)
        List<SpotBookingInfo> findBySpotInfo_SpotId(long spotId);

        // List<SpotBookingInfo> findBySlotId(long slotId);
        boolean existsByVehicle_VehicleId(Long vehicleId);

        boolean existsBySpotInfo_SpotId(Long spotId); // ✅ Fix

        List<SpotBookingInfo> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

        @Query("SELECT sb FROM SpotBookingInfo sb " +
                        "WHERE sb.spotInfo.spotId = :spotId " +
                        "AND sb.status NOT IN ('cancelled', 'completed') " +
                        "AND ((sb.startDate < :endDate) OR (sb.startDate = :endDate AND sb.startTime < :endTime)) " +
                        "AND ((sb.endDate > :startDate) OR (sb.endDate = :startDate AND sb.endTime > :startTime))")
        List<SpotBookingInfo> findConflictingBooking(
                        @Param("spotId") Long spotId,
                        @Param("startDate") LocalDate startDate,
                        @Param("startTime") LocalTime startTime,
                        @Param("endDate") LocalDate endDate,
                        @Param("endTime") LocalTime endTime);
        // To set the status of spot as unavailable and spotbookinginfo as confirmed

        // Fetch bookings where start date is today, start time has passed, and status
        // is "booked" (to be confirmed)

        @Query("SELECT s FROM SpotBookingInfo s WHERE s.startDate = :currentDate AND s.startTime <= :currentTime AND s.status = 'booked'")

        List<SpotBookingInfo> findBookingsToConfirm(@Param("currentDate") LocalDate currentDate,
                        @Param("currentTime") LocalTime currentTime);

        // Fetch bookings where end time has passed and status is "confirmed" (to be
        // completed)

        @Query("SELECT s FROM SpotBookingInfo s WHERE s.endDate = :currentDate AND s.endTime <= :currentTime AND s.status = 'confirmed'")

        List<SpotBookingInfo> findBookingsToComplete(@Param("currentDate") LocalDate currentDate,
                        @Param("currentTime") LocalTime currentTime);

        @Query("SELECT b FROM SpotBookingInfo b WHERE b.spotInfo.spotId = :spotId " +
                        "AND b.bookingId != :excludeBookingId " +
                        "AND b.status NOT IN ('cancelled', 'completed') " +
                        "AND NOT (:endDate < b.startDate OR (:endDate = b.startDate AND :endTime <= b.startTime)) " +
                        "AND NOT (:startDate > b.endDate OR (:startDate = b.endDate AND :startTime >= b.endTime))")
        List<SpotBookingInfo> findConflictingBookingsforUpdatedbookings(
                        long spotId, LocalDate startDate, LocalTime startTime,
                        LocalDate endDate, LocalTime endTime, long excludeBookingId);

        @Query("SELECT sb FROM SpotBookingInfo sb " +
                        "WHERE sb.vehicle.userObj.contactNumber = :contactNumber " +
                        "AND sb.status = 'cancelled'")
        List<SpotBookingInfo> findCancelledBookingsByContactNumber(@Param("contactNumber") String contactNumber);

        @Query("SELECT s.spotInfo FROM SpotBookingInfo s " +
                        "JOIN s.spotInfo sp JOIN sp.location l " +
                        "WHERE s.status = 'CONFIRMED' " +
                        "AND (:city IS NULL OR l.city = :city) " +
                        "AND (:landmark IS NULL OR l.landmark = :landmark) ")
        List<Spot> findByCityAndLandmark(@Param("city") String city,
                        @Param("landmark") String landmark);

}