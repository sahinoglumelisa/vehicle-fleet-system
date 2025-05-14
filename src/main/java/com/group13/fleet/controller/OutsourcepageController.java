package com.group13.fleet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OutsourcepageController {
    @GetMapping("/outsource/management-vehicles")
    public String showVehicleManagementDashboard() {
        return "outsource-management-vehicles";
    }
    @GetMapping("/outsource")
    public String showOutsourcePage(Model model) {
        return "outsource";
    }

    @GetMapping("/outsource/odometer-entry-tracking")
    public String showOdometerEntryTracking(Model model) {
        return "outsource-odometer-entry-tracking"; // odometer-entry-tracking.html
    }

    @GetMapping("/outsource/report-prediction")
    public String showReportsAndPrediction(Model model) {
        return "outsource-report-prediction"; // report-prediction.html
    }
}