package com.qpa.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qpa.entity.AddOns;
import com.qpa.entity.VehicleType;
import com.qpa.service.AddOnsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/addons")
public class AddOnController {
    @Autowired
    private final AddOnsService addOnService;

    public AddOnController(AddOnsService addOnService) {
        this.addOnService = addOnService;
    }

    @GetMapping("/viewAll")
    public List<AddOns> getAllAddOns() {
        return addOnService.getAllAddOns();
    }

    @GetMapping("/{id}")
    public AddOns getAddOnById(@PathVariable Long id) {
        return addOnService.getAddOnsById(id);
    }

    @GetMapping("/vehicle-type/{vehicleType}")
    public List<AddOns> getAddOnsByVehicleType(@PathVariable VehicleType vehicleType) {
        return addOnService.getAddOnsByVehicleType(vehicleType);
    }

    @PostMapping("/add")
    public AddOns addAddOn(@Valid @RequestBody AddOns addOn) {
        return addOnService.saveAddOns(addOn);
    }

    @PutMapping("/update/{id}")
    public AddOns updateAddOn(@PathVariable Long id, @Valid @RequestBody AddOns updatedAddOn) {
        return addOnService.updateAddOn(id, updatedAddOn);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteAddOn(@PathVariable Long id) {
        addOnService.deleteAddOn(id);
        return "AddOn with ID " + id + " has been deleted.";
    }

}
