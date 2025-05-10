package com.group13.fleet.controller;

import com.group13.fleet.entity.Vehicle;
import com.group13.fleet.repository.VehicleRepository;
import com.group13.fleet.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class DashboardController {
    @Autowired
    private VehicleRepository vehicleRepository;

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("vehicles", vehicleRepository.findAll());
        System.out.println(model.getAttribute("vehicles"));
        return "dashboard";
    }
}


