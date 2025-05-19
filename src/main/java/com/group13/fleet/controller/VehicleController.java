package com.group13.fleet.controller;

import com.group13.fleet.entity.*;
import com.group13.fleet.repository.CustomerRepository;
import com.group13.fleet.repository.VehicleRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class VehicleController {
    private VehicleRepository vehicleRepository;
    private CustomerRepository customerRepository;

    @Autowired
    public VehicleController(VehicleRepository vehicleRepository, CustomerRepository customerRepository) {
        this.vehicleRepository = vehicleRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/vehicle")
    public String getVehicleByPlate(@RequestParam("plateNumber") String plateNumber, Model model) {
        Vehicle vehicle = vehicleRepository.findByPlateNumber(plateNumber);
        if (vehicle == null) {
            return "redirect:/vehicles"; // or show an error page
        }
        model.addAttribute("vehicle", vehicle);
        return "vehicle";
    }

    @GetMapping("/vehicles")
    public String getVehicleDetail(@RequestParam("id") String plateNumber, Model model) {
        Vehicle vehicleOpt = vehicleRepository.findByPlateNumber(plateNumber);
            model.addAttribute("vehicle", vehicleOpt);
            return "vehicle-detail"; // vehicle-detail.html
    }


    @PostMapping("/vehicle/addVehicle")
    public String addVehicleToCustomer(HttpSession session, RedirectAttributes redirectAttributes, @RequestParam("vehicleId") Integer vehicleId) {
        try {
            Integer customerId = (Integer) session.getAttribute("customerId");

            Vehicle vehicle = vehicleRepository.findByVehicleId(vehicleId);

            vehicle.setCustomer(customerId);
            vehicle.setStatus(VehicleStatus.AVAILABLE); // or any appropriate value

            vehicleRepository.save(vehicle);

            redirectAttributes.addFlashAttribute("success", "Vehicle added successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while assigning the vehicle.");
        }

        return "redirect:/customer/dashboard"; // or wherever the user should go next
    }

    @PostMapping("/vehicle/addOwned")
    public String addOwnedVehicle(HttpSession session,
                                  @ModelAttribute("vehicle") Vehicle vehicle,
                                  Model model) {
        Integer customerId = (Integer) session.getAttribute("customerId");

        if (vehicleRepository.existsByPlateNumber(vehicle.getPlateNumber())) {
            model.addAttribute("plateError", "There is already vehicle with that plate number.");
            model.addAttribute("vehicle", vehicle);
            model.addAttribute("customerId", customerId);
            return "customer-owned-vehicle"; // bu HTML dosyanın tam Thymeleaf adı
        }

        vehicle.setCustomer(customerId);
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        vehicle.setOwnershipType(OwnershipType.OWNED);
        vehicle.setCurrentOdometer(0.0);
        vehicle.setPreviousMonthOdometer(vehicle.getCurrentOdometer());
        vehicle.setDriver(null);

        vehicleRepository.save(vehicle);

        return "redirect:/customer/dashboard";
    }


    @PostMapping("/vehicle/pool/{vehicleId}")
    public String assignVehicleJob(HttpSession session, RedirectAttributes redirectAttributes,@PathVariable("vehicleId") Integer vehicleId) {
        try {
            Integer customerId = (Integer) session.getAttribute("customerId");

            Vehicle vehicle = vehicleRepository.findByVehicleId(vehicleId);
            vehicle.setStatus(VehicleStatus.WAITING_FOR_ASSIGNMENT);
            vehicleRepository.save(vehicle);

            redirectAttributes.addFlashAttribute("success", "Vehicle added successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while assigning the vehicle.");
        }

        return "redirect:/customer/dashboard"; // or wherever the user should go next
    }
}
