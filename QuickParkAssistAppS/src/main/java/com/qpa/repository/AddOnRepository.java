package com.qpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qpa.entity.AddOns;
import com.qpa.entity.VehicleType;

public interface AddOnRepository extends JpaRepository<AddOns, Long> {
	List<AddOns> findByVehicleType(VehicleType vehicleType);  //Purpose: Defines a custom query method to fetch a list of AddOn entities filtered by vehicleType.Spring Data automatically generates the query based on the method name.The method name findByVehicleType corresponds to the query condition WHERE vehicle_type = ? in SQL.Accepts a String parameter vehicleType, which maps to the vehicleType field in the AddOn entity.Returns a List<AddOn> containing all AddOn entities with the matching vehicleType.
}
