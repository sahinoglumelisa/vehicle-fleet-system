package com.group13.fleet.controller;

import com.group13.fleet.entity.Vehicle;
import com.group13.fleet.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class VehicleController {
    private VehicleRepository vehicleRepository;

    @Autowired
    public VehicleController(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @GetMapping("/vehicle")
    public String getVehicleByPlate(@RequestParam("plateNumber") String plateNumber, Model model) {
        Vehicle vehicle = vehicleRepository.findByPlateNumber(plateNumber);
        if (vehicle == null) {
            return "redirect:/vehicles"; // or show an error page
        }
        model.addAttribute("vehicle", vehicle);
        return "vehicle";
    }
}
