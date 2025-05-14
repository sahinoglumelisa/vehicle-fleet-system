package com.group13.fleet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomerController {

    @GetMapping("/customer")
    public String showCustomerMenu(Model model) {
        return "customer"; // 3 butonlu sayfa
    }

    @GetMapping("/customer/vehicle-management")
    public String showCustomerVehicleManagement(Model model) {
        return "customer-vehicle-management";
    }

    @GetMapping("/customer/task-driver-management")
    public String showCustomerTaskDriverManagement(Model model) {
        return "customer-task-driver-management";
    }

    @GetMapping("/customer/report-prediction")
    public String showCustomerReportPrediction(Model model) {
        return "customer-report-prediction";
    }
}
