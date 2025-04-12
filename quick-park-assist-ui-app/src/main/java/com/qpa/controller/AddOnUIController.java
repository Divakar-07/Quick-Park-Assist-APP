package com.qpa.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qpa.entity.AddOns;
import com.qpa.entity.VehicleType;
import com.qpa.service.CustomRestTemplateService;

import jakarta.servlet.http.HttpServletRequest;

@Controller

@RequestMapping("/addons")
public class AddOnUIController {
    @Autowired
    private CustomRestTemplateService restTemplate;

    private static final String BASE_URL = "/addons";

    @GetMapping("/new-addon")
    public String addAddonForm(Model model) {
        model.addAttribute("addon", new AddOns());
        model.addAttribute("vehicleTypes", Arrays.asList(VehicleType.values())); // Add enum values for dropdown
        return "addons/addAddon";
    }

    @PostMapping("/addAddon")
    public String addAddon(@ModelAttribute AddOns addon, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        try {
            if (addon.getAddOnId() != null) {
                String url = BASE_URL + "/update/" + addon.getAddOnId();
                restTemplate.put(url, addon, request, new ParameterizedTypeReference<AddOns>() {
                });
                redirectAttributes.addFlashAttribute("message", "Addon updated successfully!");
                return "redirect:/addons/view-addon";
            }

            ResponseEntity<AddOns> response = restTemplate.post(BASE_URL + "/add", addon, request,
                    new ParameterizedTypeReference<AddOns>() {
                    });
            if (response.getBody() == null) {
                redirectAttributes.addFlashAttribute("message", "Error adding addon: ");
                return "redirect:/addons/addAddon";
            }
            redirectAttributes.addFlashAttribute("message",
                    "Addon added successfully: " + response.getBody().getName());
            return "redirect:/addons/view-addon";
        } catch (Exception e) {
            return "redirect:/addons/addAddon";
        }
    }

    @GetMapping("/view-addon")
    public String viewAllAddons(Model model, HttpServletRequest request) {
        ResponseEntity<List<AddOns>> response = restTemplate.get(
                BASE_URL + "/viewAll", request, new ParameterizedTypeReference<List<AddOns>>() {
                });

        List<AddOns> allAddOns = response.getBody();
        model.addAttribute("vehicleTypes", VehicleType.values());
        model.addAttribute("addons", allAddOns);
        return "addons/viewAllAddons"; // Renders viewAllAddons.html
    }

    @GetMapping("/view-addon/{addOnId}")
    public String viewAddOnById(@PathVariable Long addOnId, Model model, HttpServletRequest request) {
        try {
            ResponseEntity<AddOns> response = restTemplate.get(BASE_URL + "/" + addOnId, request,
                    new ParameterizedTypeReference<AddOns>() {
                    });
            model.addAttribute("vehicleTypes", VehicleType.values());
            model.addAttribute("addons", response.getBody());
            System.out.println(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "AddOn not found with ID: " + addOnId);
        }
        return "addons/viewAllAddons"; // Returns the Thymeleaf template
    }

    @GetMapping(value = "/view-addon", params = "vehicleType")
    public String viewAddOnsByVehicleType(@RequestParam("vehicleType") VehicleType vehicleType, Model model,
            HttpServletRequest request) {
        try {
            System.out.println("in the vehicleType controller");
            ResponseEntity<AddOns[]> response = restTemplate.get(BASE_URL + "/vehicle-type/" + vehicleType, request,
                    new ParameterizedTypeReference<AddOns[]>() {
                    });
            model.addAttribute("addons", Arrays.asList(response.getBody()));
            model.addAttribute("vehicleTypes", VehicleType.values());
            model.addAttribute("vehicleType", vehicleType);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "No AddOns found for Vehicle Type: " + vehicleType);
        }
        return "addons/viewAllAddons"; // Returns the Thymeleaf template
    }

    @GetMapping("/editAddon/{id}")
    public String editAddonForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        ResponseEntity<AddOns> response = restTemplate.get(BASE_URL + "/" + id, request,
                new ParameterizedTypeReference<AddOns>() {
                });
        model.addAttribute("addon", response.getBody());
        model.addAttribute("vehicleTypes", VehicleType.values());
        return "addons/addAddon";
    }

    @GetMapping("/deleteAddon/{id}")
    public String deleteAddon(@PathVariable Long id, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        String message = restTemplate
                .delete(BASE_URL + "/delete/" + id, null, request, new ParameterizedTypeReference<String>() {
                }).getBody();
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/addons/view-addon";
    }
}
