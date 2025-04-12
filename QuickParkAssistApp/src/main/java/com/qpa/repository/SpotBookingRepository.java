package com.qpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qpa.entity.SpotBookingInfo;

public interface SpotBookingRepository extends JpaRepository<SpotBookingInfo, Long> {

}
