package com.group13.fleet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OutsourceVehicleManagementController {

    @GetMapping("/outsource/management-vehicles/list")
    public String listVehicles() {
        return "outsource-vehicle-list";
    }

    @GetMapping("/outsource/management-vehicles/add")
    public String addVehicleForm() {
        return "outsource-vehicle-add";
    }

    @GetMapping("/outsource/management-vehicles/edit")
    public String editVehicleForm() {
        return "outsource-vehicle-edit";
    }
}

