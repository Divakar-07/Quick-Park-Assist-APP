package com.qpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qpa.entity.SpotInfo;

public interface SpotRepository extends JpaRepository<SpotInfo, Long> {

}
