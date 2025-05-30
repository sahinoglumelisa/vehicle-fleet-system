package com.group13.fleet.controller;

import com.group13.fleet.entity.*;
import com.group13.fleet.repository.*;
import com.group13.fleet.service.CustomerExpenseService;
import com.group13.fleet.service.OutsourceExpenseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private CustomerExpenseService customerExpenseService;
    @Autowired
    private FuelConsumptionRepository fuelConsumptionRepository;

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
        //Optional<VehicleUsage> vehicleUsage = vehicleUsageRepository.findFirstOngoingOrUpcomingUsage(driverId);

        List<VehicleUsage> usageList = vehicleUsageRepository.findLatestUsageByDriver(driverId);

        VehicleUsage vehicleUsage = usageList.isEmpty() ? null : usageList.get(0);
        boolean hasVehicle = vehicle.isPresent();
        boolean hasDuty = vehicleUsage!=null;
        String purpose = "";
        LocalDate startDate = null;
        LocalDate endDate = null;
        long daysLeft = 0;


        if (vehicleUsage!=null) {
            purpose = vehicleUsage.getPurpose();
            startDate = vehicleUsage.getStartDate();
            endDate = vehicleUsage.getEndDate();

            LocalDate today = LocalDate.now();
            if (!endDate.isBefore(today)) {
                daysLeft = ChronoUnit.DAYS.between(today, endDate) + 1;
            }
            System.out.println(vehicleUsage);
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
        List<VehicleUsage> usageList = vehicleUsageRepository.findLatestUsageByDriver(driverId);

        String companyName = customerRepository.findById(driver.getCompanyId()).get().getUsername().toUpperCase();
        model.addAttribute("companyName", companyName);
        model.addAttribute("hasVehicle", vehicle.isPresent());
        vehicle.ifPresent(v -> model.addAttribute("vehicle", v));
       // vehicleUsage.ifPresent(usage -> model.addAttribute("usage", usage));
        if(!usageList.isEmpty()) {
            model.addAttribute("usage", usageList.get(0));
        }

        return "enter-km";
    }

    @PostMapping("/driver/dashboard/submit-km")
    public String submitKm(@RequestParam("usageId") int usageId,
                           @RequestParam("endOdometer") double endOdometer,
                           HttpSession session,
                           Model model) {

        VehicleUsage usage = vehicleUsageRepository.findById(usageId)
                .orElseThrow(() -> new RuntimeException("Usage not found"));

        if (endOdometer <= usage.getStartOdometer()) {
            model.addAttribute("usage", usage);
            model.addAttribute("error", "End odometer must be greater than start odometer.");
            return "enter-km";  // return to the same page
        }

        usage.setEndOdometer(endOdometer);
        usage.setVerified(false);
        vehicleUsageRepository.save(usage);

        return "redirect:/driver/dashboard";
    }


    @GetMapping("/customer/dashboard")
    public String showCustomerDashboard(Model model, HttpSession session) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer customerId = (Integer) session.getAttribute("customerId");

        List<Vehicle> vehicles = vehicleRepository.findVehicleByCustomer(customerId);
        model.addAttribute("vehicles", vehicles);

        List<Driver> drivers = driverRepository.findDriversByCompanyId(customerId);
        model.addAttribute("drivers", drivers);
        System.out.println(drivers);

        model.addAttribute("totalVehicles", vehicles.size());
        model.addAttribute("activeVehicles", vehicles.stream().filter(vehicle -> vehicle.getStatus()== VehicleStatus.ASSIGNED).count());
        model.addAttribute("maintenanceVehicles", vehicles.stream().filter(vehicle -> vehicle.getStatus()==VehicleStatus.IN_SERVICE).count());

        return "customer-dashboard";
    }


    @GetMapping("/customer/dashboard/assign/driver")
    public String showAssignPage(Model model, HttpSession session) {
        Integer customerId = (Integer) session.getAttribute("customerId");
        List<Driver> drivers = driverRepository.findDriversByCompanyIdAndStatus(customerId,false);
        model.addAttribute("drivers", drivers);
        System.out.println(drivers);
        return "customer-dashboard-assign-driver";
    }

    @GetMapping("/customer/dashboard/assign/vehicle")
    public String showVehicleAssignPage(Model model, HttpSession session) {
        Integer customerId = (Integer) session.getAttribute("customerId");
        List<Vehicle> vehicles = vehicleRepository.findVehiclesByCustomerAndStatus(customerId,VehicleStatus.AVAILABLE);
        model.addAttribute("vehicles", vehicles);
        System.out.println(vehicles);
        return "customer-dashboard-assign-vehicle";
    }

    @GetMapping("/customer/dashboard/assign/driver/{driverId}")
    public String showDriverAssignmentPage(@PathVariable("driverId") Integer driverId, Model model, HttpSession session) {
        Integer customerId = (Integer) session.getAttribute("customerId");
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid driver ID: " + driverId));

        model.addAttribute("driver", driver);

        List<Vehicle> availableVehicles = vehicleRepository.findByCustomerAndStatuses(customerId,List.of(VehicleStatus.AVAILABLE,VehicleStatus.WAITING_FOR_ASSIGNMENT));
        System.out.println(availableVehicles);
        model.addAttribute("availableVehicles", availableVehicles);
        return "assign-job-driver";
    }

    @GetMapping("/customer/dashboard/assign/vehicle/{vehicleId}")
    public String showVehicleAssignmentPage(@PathVariable("vehicleId") Integer vehicleId, Model model) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid driver ID: " + vehicleId));

        model.addAttribute("vehicle", vehicle);
        return "assign-job-vehicle";
    }

    @GetMapping("/customer/dashboard/fleet")
    public String showFleetPage(Model model, HttpSession session) {
        Integer customerId = (Integer) session.getAttribute("customerId");
        List<Vehicle> vehicles = vehicleRepository.findVehicleByCustomerIsNull();
        model.addAttribute("vehicles", vehicles);
        return "fleet-job-customer";
    }

    @GetMapping("/customer/dashboard/newDriver")
    public String showNewDriverPage(Model model, HttpSession session) {
        Integer customerId = (Integer) session.getAttribute("customerId");
        model.addAttribute("customerId", customerId);

        Driver driver = new Driver();
        model.addAttribute("driver", driver);
        return "new-driver-page";
    }

    @GetMapping("/customer/dashboard/ownedVehicle")
    public String showOwnedVehiclePage(Model model, HttpSession session) {
        Integer customerId = (Integer) session.getAttribute("customerId");
        Vehicle newVehicle = new Vehicle();

        model.addAttribute("vehicle", newVehicle);
        model.addAttribute("customerId", customerId);
        return "customer-owned-vehicle";
    }

    @GetMapping("/customer/dashboard/report")
    public String showReportsPage(Model model, HttpSession session) {
        return "customer-dashboard-report";
    }

    @GetMapping("/customer/dashboard/report/monthly-summary")
    public String showMonthlySummary(HttpSession session,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            Model model) {
        Integer customerId = (Integer) session.getAttribute("customerId");

        // Use current date as fallback
        LocalDate now = LocalDate.now();
        int finalYear = (year != null) ? year : now.getYear();
        int finalMonth = (month != null && month >= 1 && month <= 12) ? month : now.getMonthValue();

        LocalDate targetDate = LocalDate.of(finalYear, finalMonth, 1);

        // Format date values for model
        String monthYear = targetDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String monthName = targetDate.format(DateTimeFormatter.ofPattern("MMMM yyyy"));

        // Fetch expense-related data
        var expenseData = customerExpenseService.getMonthlyExpenseSummary(targetDate,customerId);
        var vehicleExpenses = customerExpenseService.getVehicleExpenseBreakdown(targetDate,customerId);
        var chartData = customerExpenseService.getExpenseChartData(targetDate,customerId);

        System.out.println(expenseData);

        // Populate model
        model.addAttribute("expenseData", expenseData);
        model.addAttribute("vehicleExpenses", vehicleExpenses);
        model.addAttribute("chartData", chartData);
        model.addAttribute("currentMonth", monthYear);
        model.addAttribute("monthName", monthName);

        return "company-monthly-summary";
    }

    /*
    @GetMapping("/customer/dashboard/report/vehicle-summary")
    public String showMonthlySummary(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) Integer vehicleId,
            Model model,
            HttpSession session
    ) {
        Integer customerId = (Integer) session.getAttribute("customerId");

        LocalDate now = LocalDate.now();
        int finalYear = (year != null) ? year : now.getYear();
        int finalMonth = now.getMonthValue();

        LocalDate targetDate = LocalDate.of(finalYear, finalMonth, 1);
        // Dropdown list of all leased vehicles for the company
        List<Vehicle> vehicleList = vehicleRepository.findVehicleByCustomerAndOwnershipType(customerId, OwnershipType.OWNED);
        model.addAttribute("vehicleList", vehicleList);

        if (vehicleId != null) {
            // Show stats for a specific vehicle
            Map<String, Object> expenseData = customerExpenseService.getMonthlyVehicleStats(targetDate, vehicleId);
            model.addAttribute("vehicleMode", true);
            model.addAttribute("selectedVehicleId", vehicleId);
            model.addAttribute("expenseData", expenseData);
        } else {
            // General stats for the company
            Map<String, Object> expenseData = customerExpenseService.getMonthlyExpenseSummary(targetDate, customerId);
            model.addAttribute("vehicleMode", false);
            model.addAttribute("expenseData", expenseData);
        }

        List<Map<String, Object>> chartData = customerExpenseService.getExpenseChartData(targetDate, customerId);
        List<Map<String, Object>> vehicleExpenses = customerExpenseService.getVehicleExpenseBreakdown(targetDate, customerId);

        model.addAttribute("chartData", chartData);
        model.addAttribute("vehicleExpenses", vehicleExpenses);
        model.addAttribute("monthName", targetDate.format(DateTimeFormatter.ofPattern("MMM yyyy", new Locale("tr"))));
        model.addAttribute("currentMonth", targetDate.toString());

        return "customer-monthly-summary-for-vehicle";
    }

     */

    @GetMapping("/customer/dashboard/report/vehicle-summary")
    public String showVehicleExpenses(
            @RequestParam(required = false) Integer vehicleId,
            HttpSession session,
            Model model
    ) {
        Integer customerId = (Integer) session.getAttribute("customerId");

        List<Vehicle> vehicleList = vehicleRepository.findVehicleByCustomerAndOwnershipType(customerId,OwnershipType.OWNED);
        model.addAttribute("vehicleList", vehicleList);

        if (vehicleId != null) {
            LocalDate now = LocalDate.now().withDayOfMonth(1); // current month
            Map<String, Object> expenseData = customerExpenseService.getMonthlyVehicleStats(now, customerId,vehicleId);
            Optional<Vehicle> vehicle = vehicleRepository.findById(vehicleId);
            String plate = vehicle.get().getPlateNumber() + " - " + vehicle.get().getBrand() + " " + vehicle.get().getModel();

            System.out.println(expenseData);
            model.addAttribute("expenseData", expenseData);
            model.addAttribute("selectedVehicleId", vehicleId);
            model.addAttribute("selectedVehiclePlate", plate);
            List<FuelConsumption> fuelConsumptions = fuelConsumptionRepository.findByVehicle(vehicleId);
            model.addAttribute("fuelHistory", fuelConsumptions);

        }




        return "customer-monthly-summary-for-vehicle"; // your HTML file
    }

    @GetMapping("/customer/dashboard/report/vehicle-summary-leased")
    public String showVehicleExpensesLeased(
            @RequestParam(required = false) Integer vehicleId,
            HttpSession session,
            Model model
    ) {
        Integer customerId = (Integer) session.getAttribute("customerId");

        List<Vehicle> vehicleList = vehicleRepository.findVehicleByCustomerAndOwnershipType(customerId,OwnershipType.LEASED);
        model.addAttribute("vehicleList", vehicleList);

        if (vehicleId != null) {
            LocalDate now = LocalDate.now().withDayOfMonth(1); // current month
            Map<String, Object> expenseData = customerExpenseService.getMonthlyVehicleStats(now, customerId,vehicleId);
            Optional<Vehicle> vehicle = vehicleRepository.findById(vehicleId);
            String plate = vehicle.get().getPlateNumber() + " - " + vehicle.get().getBrand() + " " + vehicle.get().getModel();

            System.out.println(expenseData);
            model.addAttribute("expenseData", expenseData);
            model.addAttribute("selectedVehicleId", vehicleId);
            model.addAttribute("selectedVehiclePlate", plate);
            List<FuelConsumption> fuelConsumptions = fuelConsumptionRepository.findByVehicle(vehicleId);
            model.addAttribute("fuelHistory", fuelConsumptions);

        }




        return "customer-monthly-summary-for-vehicle"; // your HTML file
    }
    @PostMapping("/customer/dashboard/remove-from-fleet")
    public String removeFromFleet(@RequestParam("vehicleId") int vehicleId, RedirectAttributes redirectAttributes) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            redirectAttributes.addFlashAttribute("error", "❌ Only AVAILABLE vehicles can be removed.");
            return "redirect:/customer/dashboard";
        }

        if (vehicle.getOwnershipType() == OwnershipType.OWNED) {
            vehicleRepository.delete(vehicle);
        } else {
            vehicle.setCustomer(null);
            vehicle.setDriver(null);
            vehicle.setStatus(VehicleStatus.AVAILABLE);
            vehicleRepository.save(vehicle);
        }

        redirectAttributes.addFlashAttribute("success", "✅ Vehicle successfully removed.");
        return "redirect:/customer/dashboard";
    }








}


