package com.group13.fleet.controller;

import com.group13.fleet.entity.Customer;
import com.group13.fleet.entity.Vehicle;
import com.group13.fleet.entity.VehicleStatus;
import com.group13.fleet.repository.CustomerRepository;
import com.group13.fleet.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class HomepageController {
    private VehicleRepository vehicleRepository;
    private CustomerRepository customerRepository;

    @Autowired
    public HomepageController(VehicleRepository vehicleRepository, CustomerRepository customerRepository) {
        this.vehicleRepository = vehicleRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/")
    public String getHomePage(Model model) {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        model.addAttribute("vehicles", vehicles);
        Optional<Customer> customer = customerRepository.findById(1);
        System.out.println(vehicles);
        return "homepage";
    }
}
