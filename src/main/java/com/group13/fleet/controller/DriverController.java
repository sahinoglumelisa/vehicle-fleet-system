package com.group13.fleet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DriverController {

    @GetMapping("/driver")
    public String showDriverPage(Model model) {
        return "driver";
    }

    @GetMapping("/driver/task")
    public String showTaskPage(Model model) {
        model.addAttribute("vehicle", "Ford Transit");
        model.addAttribute("taskDescription", "Deliver supplies to location X");
        model.addAttribute("startTime", "2025-05-13 08:00");
        model.addAttribute("endTime", "2025-05-13 14:00");
        return "driver-task";
    }

    @GetMapping("/driver/enter-odometer")
    public String showOdometerEntryPage(Model model) {
        return "driver-enter-odometer";
    }
}
