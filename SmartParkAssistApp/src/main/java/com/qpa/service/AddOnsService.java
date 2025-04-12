package com.qpa.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qpa.entity.AddOns;
import com.qpa.entity.VehicleType;
import com.qpa.repository.AddOnRepository;

@Service
public class AddOnsService {
	@Autowired
	private final AddOnRepository addOnRepository;

	public AddOnsService(AddOnRepository addOnRepository) {
		this.addOnRepository = addOnRepository;
	}

	public List<AddOns> getAllAddOns() {
		return addOnRepository.findAll();
	}

	public AddOns getAddOnsById(Long id) {
		return addOnRepository.findById(id).orElseThrow(() -> new RuntimeException("AddOn not found"));
	}

	public List<AddOns> getAddOnsByVehicleType(VehicleType vehicleType) {
		return addOnRepository.findByVehicleType(vehicleType);
	}

	public AddOns saveAddOns(AddOns addOns) {
		return addOnRepository.save(addOns);

	}

	public AddOns updateAddOn(Long id, AddOns updatedAddOn) {
		AddOns existingAddOn = addOnRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("AddOn not found with id: " + id));

		existingAddOn.setName(updatedAddOn.getName());
		existingAddOn.setDescription(updatedAddOn.getDescription());
		existingAddOn.setPrice(updatedAddOn.getPrice());
		existingAddOn.setVehicleType(updatedAddOn.getVehicleType());

		return addOnRepository.save(existingAddOn);
	}

	public void deleteAddOn(Long id) {
		if (!addOnRepository.existsById(id)) {
			throw new RuntimeException("AddOn not found with id: " + id);
		}
		addOnRepository.deleteById(id);
	}

}
