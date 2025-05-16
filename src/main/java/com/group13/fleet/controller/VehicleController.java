package com.group13.fleet.controller;

import com.group13.fleet.entity.Customer;
import com.group13.fleet.entity.Vehicle;
import com.group13.fleet.entity.VehicleStatus;
import com.group13.fleet.repository.CustomerRepository;
import com.group13.fleet.repository.VehicleRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class VehicleController {
    private VehicleRepository vehicleRepository;
    private CustomerRepository customerRepository;

    @Autowired
    public VehicleController(VehicleRepository vehicleRepository, CustomerRepository customerRepository) {
        this.vehicleRepository = vehicleRepository;
        this.customerRepository = customerRepository;
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

    @GetMapping("/vehicles")
    public String getVehicleDetail(@RequestParam("id") String plateNumber, Model model) {
        Vehicle vehicleOpt = vehicleRepository.findByPlateNumber(plateNumber);
            model.addAttribute("vehicle", vehicleOpt);
            return "vehicle-detail"; // vehicle-detail.html
    }


    @PostMapping("/addVehicle")
    public String addVehicleToCustomer(HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            // 1. Get logged-in user's email from Spring Security context
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String email;

            if (principal instanceof UserDetails) {
                email = ((UserDetails) principal).getUsername();
            } else {
                email = principal.toString();
            }

            // 2. Find the customer by email
            Customer customer = customerRepository.findByEmail(email);
            if (customer == null) {
                redirectAttributes.addFlashAttribute("error", "Customer not found.");
                return "redirect:/";
            }

            // 3. Example: Get a vehicle (in real case you’d probably receive an ID)
            // Here we're assuming you want to attach the latest "unassigned" vehicle.
            Vehicle vehicle = vehicleRepository.findFirstByCustomerIsNull(); // You can customize this
            if (vehicle == null) {
                redirectAttributes.addFlashAttribute("error", "No available vehicle to assign.");
                return "redirect:/";
            }

            // 4. Assign vehicle to customer and update status
            vehicle.setCustomer(customer.getCompanyId());
            vehicle.setStatus(VehicleStatus.ASSIGNED); // or any appropriate value

            vehicleRepository.save(vehicle);

            redirectAttributes.addFlashAttribute("success", "Vehicle added successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while assigning the vehicle.");
        }

        return "redirect:/dashboard"; // or wherever the user should go next
    }
}
