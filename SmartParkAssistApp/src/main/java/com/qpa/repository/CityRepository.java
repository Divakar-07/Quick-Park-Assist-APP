package com.qpa.repository;

import com.qpa.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    List<City> findByStateName(String stateName);
    City findByName(String name);
    boolean existsByName(String name);

    @Query("SELECT DISTINCT c.stateName FROM City c ORDER BY c.stateName")
    List<String> findAllUniqueStates();

    @Query("SELECT DISTINCT c.name FROM City c ORDER BY c.name")
    List<String> findAllUniqueCities();
}
