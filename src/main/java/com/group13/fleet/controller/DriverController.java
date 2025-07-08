package com.group13.fleet.controller;

import com.group13.fleet.entity.Driver;
import com.group13.fleet.entity.Vehicle;
import com.group13.fleet.entity.VehicleStatus;
import com.group13.fleet.entity.VehicleUsage;
import com.group13.fleet.repository.DriverRepository;
import com.group13.fleet.repository.VehicleRepository;
import com.group13.fleet.repository.VehicleUsageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
public class DriverController {

    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleUsageRepository vehicleUsageRepository;

    @Autowired
    public DriverController(DriverRepository driverRepository, VehicleRepository vehicleRepository, VehicleUsageRepository vehicleUsageRepository) {
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
        this.vehicleUsageRepository = vehicleUsageRepository;
    }

    @GetMapping("/driver")
    public String showDriverPage(Model model) {
        return "driver";
    }

    @GetMapping("/driver/task")
    public String showTaskPage(Model model) {
        model.addAttribute("vehicle", "Ford Transit");
        model.addAttribute("taskDescription", "Deliver supplies to location X");
        model.addAttribute("startTime", "2025-05-13 08:00");
        model.addAttribute("endTime", "2025-05-13 14:00");
        return "driver-task";
    }

    @GetMapping("/driver/enter-odometer")
    public String showOdometerEntryPage(Model model) {
        return "driver-enter-odometer";
    }

    @PostMapping("/driver/assign-vehicle")
    public String assignVehicle(@RequestParam Integer driverId,
                                @RequestParam Integer vehicleId,
                                @RequestParam String purpose) {  // 👈 Add this line
        Driver driver = driverRepository.findByUserId(driverId);
        Vehicle vehicle = vehicleRepository.findByVehicleId(vehicleId);

        driver.setActive(Boolean.TRUE);
        driverRepository.save(driver);

        vehicle.setDriver(driver.getUserId());
        vehicle.setStatus(VehicleStatus.ASSIGNED);
        vehicleRepository.save(vehicle);

        VehicleUsage vehicleUsage = new VehicleUsage(
                null,
                vehicleId,
                driverId,
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                vehicle.getCurrentOdometer(),
                vehicle.getCurrentOdometer(),
                purpose,   // 👈 Use the purpose value here
                false
        );

        vehicleUsageRepository.save(vehicleUsage);

        return "redirect:/customer/dashboard";
    }

    @PostMapping("/driver/create")
    public String createDriver(@RequestParam Integer customerId,
                               @ModelAttribute("driver") Driver driver,
                               Model model) {

        String finalUsername = "driver_" + driver.getUsername();
        boolean usernameExists = driverRepository.existsByUsername(finalUsername);
        boolean emailExists = driverRepository.existsByEmail(driver.getEmail());
        boolean licenseExists = driverRepository.existsByLicenseNumber(driver.getLicenseNumber());

        boolean hasError = false;

        if (usernameExists) {
            model.addAttribute("usernameError", "This username is already taken.");
            hasError = true;
        }
        if (emailExists) {
            model.addAttribute("emailError", "This email is already in use.");
            hasError = true;
        }
        if (licenseExists) {
            model.addAttribute("licenseError", "This license number is already registered.");
            hasError = true;
        }

        if (hasError) {
            driver.setUsername(driver.getUsername()); // preserve unprefixed username
            model.addAttribute("driver", driver);
            model.addAttribute("customerId", customerId);
            return "customer/new-driver"; // your Thymeleaf page
        }

        driver.setUsername(finalUsername);
        driver.setCompanyId(customerId);
        driver.setActive(Boolean.FALSE);
        driverRepository.save(driver);

        return "redirect:/customer/dashboard";
    }



}
