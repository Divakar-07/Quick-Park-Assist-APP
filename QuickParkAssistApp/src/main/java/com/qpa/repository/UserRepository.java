package com.qpa.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.qpa.entity.UserInfo;

public interface UserRepository extends JpaRepository<UserInfo, Long> {
    Optional<UserInfo> findByEmail(String email);
    List<UserInfo> findByUserType(String userType);//02-04

    Optional<UserInfo> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM UserInfo u " +
            "LEFT JOIN SpotBookingInfo b ON b.vehicle.userObj.userId = u.userId AND b.bookingDate >= :cutoffDate " +
            "WHERE u.status = 'ACTIVE' AND b.bookingId IS NULL")
    List<UserInfo> findInactiveUsers(@Param("cutoffDateTime") LocalDate cutoffDate);
}