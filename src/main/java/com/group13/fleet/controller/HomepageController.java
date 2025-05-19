package com.group13.fleet.controller;

import com.group13.fleet.entity.Customer;
import com.group13.fleet.entity.Vehicle;
import com.group13.fleet.entity.VehicleStatus;
import com.group13.fleet.repository.CustomerRepository;
import com.group13.fleet.repository.VehicleRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    public String getHomePage(Model model, HttpSession session) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer customerId = (Integer) session.getAttribute("customerId");

        List<Vehicle> vehicles = vehicleRepository.findByStatusAndCustomerIsNull(VehicleStatus.AVAILABLE);
        model.addAttribute("vehicles", vehicles);
        List<Vehicle> myVehicles = vehicleRepository.findVehicleByCustomer(customerId);
        System.out.println(myVehicles);
        model.addAttribute("myVehicles", myVehicles);
        System.out.println(vehicles);
        return "homepage";
    }
}
