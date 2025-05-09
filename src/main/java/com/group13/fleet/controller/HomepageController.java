package com.group13.fleet.controller;

import com.group13.fleet.entity.Company;
import com.group13.fleet.entity.Vehicle;
import com.group13.fleet.repository.CompanyRepository;
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
    private CompanyRepository companyRepository;

    @Autowired
    public HomepageController(VehicleRepository vehicleRepository, CompanyRepository companyRepository) {
        this.vehicleRepository = vehicleRepository;
        this.companyRepository = companyRepository;
    }

    @GetMapping("/")
    public String getHomePage(Model model) {
        List<Vehicle> vehicles = vehicleRepository.findVehicleByIsActive(false);
        model.addAttribute("vehicles", vehicles);
        Optional<Company> company = companyRepository.findById(1);

        if (company.isPresent()) {
            List<Vehicle> companyVehicles = vehicleRepository.findVehicleByCompanyId(company.get().getId());
            model.addAttribute("companyVehicles", companyVehicles);
        }
        System.out.println(vehicles);
        return "homepage";
    }
}
