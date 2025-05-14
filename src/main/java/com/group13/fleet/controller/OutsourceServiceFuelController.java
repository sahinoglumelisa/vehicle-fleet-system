package com.group13.fleet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OutsourceServiceFuelController {
    @GetMapping("/outsource/service-fuel-management")
    public String showServiceFuelManagement(Model model) {
        return "outsource-service-fuel-management"; // service-fuel-management.html
    }

    @GetMapping("/outsource/service-fuel-management/service-entry")
    public String showServiceEntryForm(Model model) {
        return "outsource-service-entry"; // service-entry.html
    }

    @GetMapping("/outsource/service-fuel-management/fuel-tracking")
    public String showFuelTrackingPage(Model model) {
        return "outsource-fuel-tracking"; // fuel-tracking.html
    }
}
