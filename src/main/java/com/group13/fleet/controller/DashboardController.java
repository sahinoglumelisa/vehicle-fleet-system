package com.group13.fleet.controller;

import com.group13.fleet.entity.Driver;
import com.group13.fleet.entity.Vehicle;
import com.group13.fleet.entity.VehicleUsage;
import com.group13.fleet.repository.CustomerRepository;
import com.group13.fleet.repository.DriverRepository;
import com.group13.fleet.repository.VehicleRepository;
import com.group13.fleet.repository.VehicleUsageRepository;
import com.group13.fleet.service.VehicleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DashboardController {
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private VehicleUsageRepository vehicleUsageRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("vehicles", vehicleRepository.findAll());
        System.out.println(model.getAttribute("vehicles"));
        return "dashboard";
    }

    @GetMapping("/driver/dashboard")
    public String showDriverDashboard(Model model, HttpSession session) {
        Integer driverId = (Integer) session.getAttribute("driverId");

        Optional<Driver> driver = driverRepository.findById(driverId);
        List<VehicleUsage> usageList = vehicleUsageRepository.findAllByDriver(driverId);

        long usageDays = usageList.stream()
                .mapToLong(u -> ChronoUnit.DAYS.between((Temporal) u.getStartDate(), (Temporal) u.getEndDate()))
                .sum();

        Double totalKmDriven = usageList.stream()
                .mapToDouble(VehicleUsage::getDrivenKm)
                .sum();

        long daysUntilExpiry = ChronoUnit.DAYS.between((Temporal) LocalDate.now(), (Temporal) driver.get().getLicenseExpiryDate());


        Map<String, Long> daysPerMonth = new TreeMap<>();
        Map<String, Double> kmPerMonth = new TreeMap<>();

        // Extract keys (months) and values to lists
        List<String> monthLabels = new ArrayList<>(daysPerMonth.keySet());
        List<Long> monthlyUsageDays = monthLabels.stream()
                .map(daysPerMonth::get)
                .collect(Collectors.toList());

        List<Double> monthlyKmDriven = monthLabels.stream()
                .map(kmPerMonth::get)
                .collect(Collectors.toList());

// Add to model
        model.addAttribute("monthLabels", monthLabels);
        model.addAttribute("monthlyUsageDays", monthlyUsageDays);
        model.addAttribute("monthlyKmDriven", monthlyKmDriven);


        List<VehicleUsage> usages = vehicleUsageRepository.findAllByDriver(driverId);
        for (VehicleUsage usage : usages) {
            LocalDate start = usage.getStartDate();
            LocalDate end = usage.getEndDate();
            double distance = usage.getEndOdometer() - usage.getStartOdometer();

            // Group by year-month
            YearMonth month = YearMonth.from(start);
            String key = month.toString();  // e.g., "2024-03"

            long days = ChronoUnit.DAYS.between(start, end) + 1;

            daysPerMonth.put(key, daysPerMonth.getOrDefault(key, 0L) + days);
            kmPerMonth.put(key, kmPerMonth.getOrDefault(key, 0.0) + distance);
        }
        System.out.println("DAYS PER MONTH: " + daysPerMonth);
        System.out.println("KM PER MONTH: " + kmPerMonth);

        String companyName = customerRepository.findById(driver.get().getCompanyId()).get().getUsername().toUpperCase();
        model.addAttribute("companyName", companyName);
        model.addAttribute("daysPerMonth", daysPerMonth);
        model.addAttribute("kmPerMonth", kmPerMonth);

        model.addAttribute("usageDays", usageDays);
        model.addAttribute("totalKmDriven", totalKmDriven);
        model.addAttribute("licenseExpiryDate", driver.get().getLicenseExpiryDate());
        model.addAttribute("daysUntilExpiry", daysUntilExpiry);
        return "driver-dashboard";
    }



    @GetMapping("/driver/dashboard/duty-and-car")
    public String showDuty(Model model, HttpSession session) {
        Integer driverId = (Integer) session.getAttribute("driverId");
        Driver driver = driverRepository.findById(driverId).orElse(null);

        if (driver == null) {
            return "redirect:/login";
        }

        Optional<Vehicle> vehicle = vehicleRepository.findVehicleByDriver(driver.getUserId());
        Optional<VehicleUsage> vehicleUsage = vehicleUsageRepository.findFirstOngoingOrUpcomingUsage(driver.getUserId());

        boolean hasVehicle = vehicle.isPresent();
        boolean hasDuty = vehicleUsage.isPresent();
        String purpose = "";
        LocalDate startDate = null;
        LocalDate endDate = null;
        long daysLeft = 0;

        if (vehicleUsage.isPresent()) {
            VehicleUsage usage = vehicleUsage.get();
            purpose = usage.getPurpose();
            startDate = usage.getStartDate();
            endDate = usage.getEndDate();

            LocalDate today = LocalDate.now();
            if (!endDate.isBefore(today)) {
                daysLeft = ChronoUnit.DAYS.between(today, endDate) + 1;
            }
        }

        String companyName = customerRepository.findById(driver.getCompanyId()).get().getUsername().toUpperCase();
        model.addAttribute("companyName", companyName);

        model.addAttribute("hasVehicle", hasVehicle);
        model.addAttribute("hasDuty", hasDuty);
        vehicle.ifPresent(v -> model.addAttribute("vehicle", v));

        model.addAttribute("duty", purpose);
        model.addAttribute("dutyStartDate", startDate);
        model.addAttribute("dutyEndDate", endDate);
        model.addAttribute("dutyDaysLeft", daysLeft);

        return "duty-and-car";
    }

    @GetMapping("/driver/dashboard/enter-km")
    public String showKm(Model model, HttpSession session) {
        Integer driverId = (Integer) session.getAttribute("driverId");
        if (driverId == null) {
            return "redirect:/login";
        }

        Optional<Driver> driverOpt = driverRepository.findById(driverId);
        if (driverOpt.isEmpty()) {
            return "redirect:/login";
        }

        Driver driver = driverOpt.get();
        Optional<Vehicle> vehicle = vehicleRepository.findVehicleByDriver(driver.getUserId());
        Optional<VehicleUsage> vehicleUsage = vehicleUsageRepository.findFirstOngoingOrUpcomingUsage(driver.getUserId());

        String companyName = customerRepository.findById(driver.getCompanyId()).get().getUsername().toUpperCase();
        model.addAttribute("companyName", companyName);
        model.addAttribute("hasVehicle", vehicle.isPresent());
        vehicle.ifPresent(v -> model.addAttribute("vehicle", v));
        vehicleUsage.ifPresent(usage -> model.addAttribute("usage", usage));

        return "enter-km";
    }

    @PostMapping("/driver/dashboard/submit-km")
    public String submitKm(@RequestParam("usageId") int usageId,
                           @RequestParam("endOdometer") double endOdometer,
                           HttpSession session) {

        VehicleUsage usage = vehicleUsageRepository.findById(usageId)
                .orElseThrow(() -> new RuntimeException("Usage not found"));

        usage.setEndOdometer(endOdometer);
        usage.setIsVerified(false);  // Admin henüz onaylamadı
        vehicleUsageRepository.save(usage);

        return "redirect:/driver/dashboard";
    }




}


