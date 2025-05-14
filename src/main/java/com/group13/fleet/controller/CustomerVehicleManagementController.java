package com.group13.fleet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomerVehicleManagementController {

    @GetMapping("/customer/vehicle-management/list")
    public String listVehicles() {
        return "customer-vehicle-list";
    }

    @GetMapping("/customer/vehicle-management/rent")
    public String rentVehicleForm() {
        return "customer-vehicle-rent";
    }
}

