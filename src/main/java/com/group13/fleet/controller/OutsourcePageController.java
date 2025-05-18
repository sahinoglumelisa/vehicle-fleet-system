package com.group13.fleet.controller;

import com.group13.fleet.entity.Driver;
import com.group13.fleet.entity.Vehicle;
import com.group13.fleet.entity.VehicleUsage;
import com.group13.fleet.repository.DriverRepository;
import com.group13.fleet.repository.VehicleRepository;
import com.group13.fleet.repository.VehicleUsageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class OutsourcePageController {

    @Autowired
    private VehicleUsageRepository vehicleUsageRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private DriverRepository driverRepository;

    @GetMapping("/outsource/management-vehicles")
    public String showVehicleManagementDashboard() {
        return "outsource-management-vehicles";
    }

    @GetMapping("/outsource")
    public String showOutsourcePage(Model model) {
        return "outsource";
    }

    @GetMapping("/outsource/odometer-entry-tracking")
    public String showOdometerTracking(Model model) {
        List<VehicleUsage> unverifiedUsage = vehicleUsageRepository.findAllByIsVerified(false);

        // Create maps for vehicle plate numbers and driver usernames
        Map<Integer, String> driverNames = new HashMap<>();
        Map<Integer, String> vehiclePlates = new HashMap<>();

        for (VehicleUsage usage : unverifiedUsage) {
            // Get vehicle information using vehicleId from usage
            Integer vehicleId = usage.getVehicle();
            Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
            if (optionalVehicle.isPresent()) {
                vehiclePlates.put(usage.getUsageId(), optionalVehicle.get().getPlateNumber());
            } else {
                vehiclePlates.put(usage.getUsageId(), "Unknown Vehicle");
            }

            // Get driver information using driverId from usage
            Integer driverId = usage.getDriver();
            Optional<Driver> optionalDriver = driverRepository.findById(driverId);
            if (optionalDriver.isPresent()) {
                driverNames.put(usage.getUsageId(), optionalDriver.get().getUsername());
            } else {
                driverNames.put(usage.getUsageId(), "Unknown Driver");
            }
        }

        model.addAttribute("unverifiedUsages", unverifiedUsage);
        model.addAttribute("driverNames", driverNames);
        model.addAttribute("vehiclePlates", vehiclePlates);

        return "outsource-odometer-entry-tracking";
    }

    @GetMapping("/outsource/report-prediction")
    public String showReportsAndPrediction(Model model) {
        return "outsource-report-prediction";
    }

    @PostMapping("/outsource/verify-usage")
    public String verifyUsage(@RequestParam("usageId") Integer usageId,
                              @RequestParam("approved") boolean approved) {

        Optional<VehicleUsage> optionalUsage = vehicleUsageRepository.findById(usageId);
        if (optionalUsage.isPresent()) {
            VehicleUsage usage = optionalUsage.get();

            // Mark the usage as verified
            usage.setVerified(true);
            vehicleUsageRepository.save(usage);

            if (approved) {
                // When approved, update the vehicle's current odometer reading
                Integer vehicleId = usage.getVehicle();
                Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
                if (optionalVehicle.isPresent()) {
                    Vehicle vehicle = optionalVehicle.get();
                    vehicle.setCurrentOdometer(usage.getEndOdometer());
                    vehicleRepository.save(vehicle);
                }
            }
        }

        return "redirect:/outsource/odometer-entry-tracking";
    }

    @PostMapping("/outsource/reject-usage")
    @ResponseBody
    public ResponseEntity<?> rejectUsage(@RequestParam("usageId") Integer usageId,
                                         @RequestParam("correctedOdometer") Double correctedOdometer) {
        try {
            System.out.println("Reject request - ID: " + usageId + ", Odometer: " + correctedOdometer);

            Optional<VehicleUsage> optionalUsage = vehicleUsageRepository.findById(usageId);
            if (optionalUsage.isPresent()) {
                VehicleUsage usage = optionalUsage.get();

                // Update both start and end odometer to the corrected value
                usage.setStartOdometer(correctedOdometer);
                usage.setEndOdometer(correctedOdometer);
                usage.setVerified(true);  // Mark as verified
                vehicleUsageRepository.save(usage);
                System.out.println("Usage updated successfully");

                // Update the vehicle's current odometer reading
                Integer vehicleId = usage.getVehicle();
                Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
                if (optionalVehicle.isPresent()) {
                    Vehicle vehicle = optionalVehicle.get();
                    vehicle.setCurrentOdometer(correctedOdometer);
                    vehicleRepository.save(vehicle);
                    System.out.println("Vehicle odometer updated to: " + correctedOdometer);
                } else {
                    System.out.println("WARNING: Could not find vehicle with ID: " + vehicleId);
                }

                return ResponseEntity.ok().body("{\"status\": \"success\", \"message\": \"Odometer reading updated successfully\"}");
            } else {
                System.out.println("ERROR: Usage not found with ID: " + usageId);
                return ResponseEntity.badRequest().body("{\"status\": \"error\", \"message\": \"Usage not found\"}");
            }
        } catch (Exception e) {
            System.out.println("ERROR in reject-usage: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("{\"status\": \"error\", \"message\": \"" + e.getMessage() + "\"}");
        }
    }
}