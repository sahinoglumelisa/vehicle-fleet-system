package com.group13.fleet.controller;

import com.group13.fleet.entity.OwnershipType;
import com.group13.fleet.entity.Vehicle;
import com.group13.fleet.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class OutsourceVehicleManagementController {

    @Autowired
    private VehicleRepository vehicleRepository;

    // Araç listeleme (sadece LEASED olanlar)
    @GetMapping("/outsource/management-vehicles/list")
    public String listVehicles(Model model) {
        List<Vehicle> leasedVehicles = vehicleRepository.findByOwnershipType(OwnershipType.LEASED);
        model.addAttribute("vehicles", leasedVehicles);
        return "outsource-vehicle-list";
    }

    // Araç ekleme formu
    @GetMapping("/outsource/management-vehicles/add")
    public String addVehicleForm(Model model) {
        model.addAttribute("vehicle", new Vehicle()); // form nesnesi
        return "outsource-vehicle-add";
    }

    // Araç ekleme işlemi (POST)
    @PostMapping("/outsource/management-vehicles/add")
    public String addVehicle(@ModelAttribute Vehicle vehicle) {
        vehicle.setOwnershipType(OwnershipType.LEASED); // her zaman leased
        vehicle.setCustomer(null); // filo yönetimi eklediği için müşteri yok

        // Eğer previous_month_odometer girilmemişse current ile eşitle
        if (vehicle.getPreviousMonthOdometer() == null && vehicle.getCurrentOdometer() != null) {
            vehicle.setPreviousMonthOdometer(vehicle.getCurrentOdometer());
        }

        vehicleRepository.save(vehicle);
        return "redirect:/outsource/management-vehicles/list";
    }
    @GetMapping("/outsource/management-vehicles/edit")
    public String showEditForm(@RequestParam("plate") String plate, Model model) {
        Vehicle vehicle = vehicleRepository.findByPlateNumber(plate);
        if (vehicle == null) {
            return "redirect:/outsource/management-vehicles/list";
        }
        model.addAttribute("vehicle", vehicle);
        return "outsource-vehicle-edit";
    }
    @PostMapping("/outsource/management-vehicles/edit")
    public String updateVehicle(@ModelAttribute Vehicle vehicle) {
        Vehicle existing = vehicleRepository.findByPlateNumber(vehicle.getPlateNumber());
        if (existing != null) {
            existing.setBrand(vehicle.getBrand());
            existing.setModel(vehicle.getModel());
            existing.setYear(vehicle.getYear());
            existing.setStatus(vehicle.getStatus());
            existing.setCurrentOdometer(vehicle.getCurrentOdometer());

            vehicleRepository.save(existing);
        }
        return "redirect:/outsource/management-vehicles/list";
    }


}
