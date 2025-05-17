package com.group13.fleet.controller;

import com.group13.fleet.entity.Driver;
import com.group13.fleet.entity.FuelConsumption;
import com.group13.fleet.entity.Vehicle;
import com.group13.fleet.repository.DriverRepository;
import com.group13.fleet.repository.FuelConsumptionRepository;
import com.group13.fleet.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
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

    @GetMapping("/outsource/service-fuel-management")
    public String showServiceFuelManagement(Model model) {
        return "outsource-service-fuel-management"; // service-fuel-management.html
    }

    @GetMapping("/outsource/service-fuel-management/fuel-tracking")
    public String showFuelTrackingPage(Model model) {
        // Get all vehicles and drivers for dropdowns
        List<Vehicle> vehicles = vehicleRepository.findAll();
        List<Driver> drivers = driverRepository.findAll();

        model.addAttribute("vehicles", vehicles);
        model.addAttribute("drivers", drivers);

        // Get recent fuel entries for initial display
        List<FuelConsumption> fuelEntries = fuelConsumptionRepository.findAll();
        model.addAttribute("fuelEntries", fuelEntries);

        Map<Long, String> vehicleNames = new HashMap<>();
        Map<Long, String> driverNames = new HashMap<>();

        for (FuelConsumption entry : fuelEntries) {
            Long fuelId = entry.getFuelId();
            Vehicle vehicle = vehicleRepository.findByVehicleId(entry.getVehicle());
            Driver driver = driverRepository.findByUserId(entry.getDriver());

            vehicleNames.put(fuelId, vehicle.getBrand() + " " + vehicle.getModel() + " (" + vehicle.getPlateNumber() + ")");
            driverNames.put(fuelId, driver.getUsername());
        }

        model.addAttribute("vehicleNames", vehicleNames);
        model.addAttribute("driverNames", driverNames);


        return "outsource-fuel-tracking";
    }


    @PostMapping("/fuel-tracking/save")
    public String saveFuelEntry(@ModelAttribute FuelConsumption fuelConsumption) {
        fuelConsumptionRepository.save(fuelConsumption);
        return "redirect:/outsource/service-fuel-management/fuel-tracking";

    }




}
