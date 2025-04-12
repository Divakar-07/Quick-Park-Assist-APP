package com.qpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qpa.entity.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {

}
