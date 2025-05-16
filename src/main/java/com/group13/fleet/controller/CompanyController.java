package com.group13.fleet.controller;


import com.group13.fleet.entity.Customer;
import com.group13.fleet.entity.Vehicle;
import com.group13.fleet.repository.CustomerRepository;
import com.group13.fleet.repository.VehicleRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.List;

@Controller
public class CompanyController {
    private final VehicleRepository vehicleRepository;

    private final CustomerRepository customerRepository;

    public CompanyController(CustomerRepository customerRepository, VehicleRepository vehicleRepository) {
        this.customerRepository = customerRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @GetMapping("/companies")
    public String listCompanies(Model model) {
        List<Customer> companies = customerRepository.findAll();
        model.addAttribute("companies", companies);

        companies.sort(Comparator.comparing(Customer::getCompanyId));

        List<Vehicle> vehicles = vehicleRepository.findVehicleByCustomer(1);
        model.addAttribute("vehicles", vehicles);
        return "companies";
    }
}
