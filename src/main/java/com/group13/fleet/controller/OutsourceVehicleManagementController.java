package com.group13.fleet.controller;

import com.group13.fleet.entity.Customer;
import com.group13.fleet.entity.OwnershipType;
import com.group13.fleet.entity.Vehicle;
import com.group13.fleet.repository.CustomerRepository;
import com.group13.fleet.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class OutsourceVehicleManagementController {

    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private CustomerRepository customerRepository;

    // Araç listeleme (sadece LEASED olanlar)
    @GetMapping("/outsource/management-vehicles/list")
    public String listVehicles(Model model) {

        // Step 1: Get leased vehicles
        List<Vehicle> leasedVehicles = vehicleRepository.findByOwnershipType(OwnershipType.LEASED);

// Step 2: Extract all customer IDs, including nulls
        List<Integer> customerIdsIncludingNulls = leasedVehicles.stream()
                .map(Vehicle::getCustomer) // may include nulls
                .collect(Collectors.toList());

// Step 3: Extract non-null unique IDs for lookup
        List<Integer> nonNullCustomerIds = customerIdsIncludingNulls.stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

// Step 4: Fetch customers
        Map<Integer, String> customerIdToUsername = customerRepository.findAllById(nonNullCustomerIds).stream()
                .collect(Collectors.toMap(Customer::getCompanyId, Customer::getUsername));

// Step 5: Map each original customer ID (including nulls) to usernames
        List<String> usernames = customerIdsIncludingNulls.stream()
                .map(id -> id == null ? "UNASSIGNED" : customerIdToUsername.getOrDefault(id, "Unknown"))
                .collect(Collectors.toList());

        model.addAttribute("usernames",usernames);
        System.out.println(usernames);

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

        System.out.println(vehicle);

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
