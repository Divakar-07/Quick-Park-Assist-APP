package com.qpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qpa.entity.Spot;

@Repository
public interface SpotRepository extends JpaRepository<Spot, Long> {

}
