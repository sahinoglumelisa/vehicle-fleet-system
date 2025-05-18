package com.group13.fleet.controller;

import com.group13.fleet.entity.*;
import com.group13.fleet.repository.DriverRepository;
import com.group13.fleet.repository.FuelConsumptionRepository;
import com.group13.fleet.repository.ServiceRepository;
import com.group13.fleet.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class OutsourceServiceFuelController {
    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private FuelConsumptionRepository fuelConsumptionRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @GetMapping("/outsource/service-fuel-management")
    public String showServiceFuelManagement(Model model) {
        return "outsource-service-fuel-management";
    }

    @GetMapping("/outsource/service-fuel-management/service-entry")
    public String showServiceEntryForm(Model model) {
        model.addAttribute("service", new Service());
        model.addAttribute("serviceTypes", ServiceType.values());
        model.addAttribute("vehicles", vehicleRepository.findAll());
        return "outsource-service-entry";
    }

    @PostMapping("/outsource/service-fuel-management/service-entry")
    public String submitServiceEntry(@ModelAttribute Service service, RedirectAttributes redirectAttributes) {
        try {
            service.setServiceDate(new Date());
            serviceRepository.save(service);
            redirectAttributes.addFlashAttribute("successMessage", "Service entry saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving service entry: " + e.getMessage());
        }
        return "redirect:/outsource/service-fuel-management";
    }

    @GetMapping("/outsource/service-fuel-management/fuel-tracking")
    public String showFuelTrackingPage(Model model) {
        // Get all vehicles and drivers for dropdowns
        List<Vehicle> vehicles = vehicleRepository.findAll();
        List<Driver> drivers = driverRepository.findAll();

        model.addAttribute("vehicles", vehicles);
        model.addAttribute("drivers", drivers);

        // Get all fuel entries
        List<FuelConsumption> fuelEntries = fuelConsumptionRepository.findAll();
        model.addAttribute("fuelEntries", fuelEntries);

        // Create maps for vehicle and driver names to display in the table
        Map<Long, String> vehicleNames = new HashMap<>();
        Map<Long, String> driverNames = new HashMap<>();

        for (FuelConsumption entry : fuelEntries) {
            // Assuming your FuelConsumption has getVehicleId() and getDriverId() methods
            if (entry.getVehicle() != null) {
                Vehicle vehicle = vehicleRepository.findByVehicleId(entry.getVehicle());
                if (vehicle != null) {
                    vehicleNames.put(entry.getFuelId(),
                            vehicle.getBrand() + " " + vehicle.getModel() + " (" + vehicle.getPlateNumber() + ")");
                }
            }

            if (entry.getDriver() != null) {
                Driver driver = driverRepository.findByUserId(entry.getDriver());
                if (driver != null) {
                    driverNames.put(entry.getFuelId(), driver.getUsername());
                }
            }
        }

        model.addAttribute("vehicleNames", vehicleNames);
        model.addAttribute("driverNames", driverNames);

        return "outsource-fuel-tracking";
    }

    // Fixed the mapping URL to match the form action
    @PostMapping("/outsource/service-fuel-management/fuel-tracking/save")
    public String saveFuelEntry(@RequestParam("vehicle.vehicleId") Integer vehicleId,
                                @RequestParam("driver.userId") Integer driverId,
                                @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                @RequestParam("liters") Double liters,
                                @RequestParam("cost") Double cost,
                                @RequestParam("odometerReading") Double odometerReading,
                                RedirectAttributes redirectAttributes) {
        try {
            // Create new FuelConsumption object
            FuelConsumption fuelConsumption = new FuelConsumption();

            // Set the basic properties
            fuelConsumption.setDate(java.sql.Date.valueOf(date));
            fuelConsumption.setLiters(liters);
            fuelConsumption.setCost(cost);
            fuelConsumption.setOdometerReading(odometerReading);

            // Set vehicle and driver using their IDs
            Vehicle vehicle = vehicleRepository.findByVehicleId(vehicleId);
            Driver driver = driverRepository.findByUserId(driverId);

            if (vehicle != null) {
                fuelConsumption.setVehicle(vehicleId); // Assuming you have setVehicleId method
            } else {
                throw new RuntimeException("Vehicle not found with ID: " + vehicleId);
            }

            if (driver != null) {
                fuelConsumption.setDriver(driverId); // Assuming you have setDriverId method
            } else {
                throw new RuntimeException("Driver not found with ID: " + driverId);
            }

            fuelConsumptionRepository.save(fuelConsumption);
            redirectAttributes.addFlashAttribute("successMessage", "Fuel entry saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving fuel entry: " + e.getMessage());
            e.printStackTrace(); // For debugging
        }
        return "redirect:/outsource/service-fuel-management/fuel-tracking";
    }

    // API endpoint for filtering fuel entries
    @GetMapping("/outsource/service-fuel-management/fuel-tracking/filter")
    @ResponseBody
    public List<FuelConsumption> filterFuelEntries(
            @RequestParam(required = false) Long vehicleId,
            @RequestParam(required = false) Long driverId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // You'll need to implement this method in your repository
        // For now, return all entries
        return fuelConsumptionRepository.findAll();
    }
}