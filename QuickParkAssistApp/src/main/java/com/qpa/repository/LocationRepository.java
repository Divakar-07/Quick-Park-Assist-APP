package com.qpa.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qpa.entity.Location;

@Repository
public interface  LocationRepository extends JpaRepository<Location, Long> {

}
