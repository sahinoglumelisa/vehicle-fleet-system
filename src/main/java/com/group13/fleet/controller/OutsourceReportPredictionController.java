package com.group13.fleet.controller;

import com.group13.fleet.entity.FuelConsumption;
import com.group13.fleet.entity.dto.OutsourceExpenseDTO;
import com.group13.fleet.repository.FuelConsumptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.group13.fleet.service.OutsourceExpenseService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class OutsourceReportPredictionController {

    @Autowired
    private OutsourceExpenseService outsourceExpenseService;
    @Autowired
    private FuelConsumptionRepository fuelConsumptionRepository;

    @GetMapping("/outsource/report-prediction/monthly-summary")
    public String showMonthlySummary(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            Model model) {

        // Use current date as fallback
        LocalDate now = LocalDate.now();
        int finalYear = (year != null) ? year : now.getYear();
        int finalMonth = (month != null && month >= 1 && month <= 12) ? month : now.getMonthValue();

        LocalDate targetDate = LocalDate.of(finalYear, finalMonth, 1);

        // Format date values for model
        String monthYear = targetDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String monthName = targetDate.format(DateTimeFormatter.ofPattern("MMMM yyyy"));

        // Fetch expense-related data
        var expenseData = outsourceExpenseService.getMonthlyExpenseSummary(targetDate);
        var vehicleExpenses = outsourceExpenseService.getVehicleExpenseBreakdown(targetDate);
        var chartData = outsourceExpenseService.getExpenseChartData(targetDate);

        // Populate model
        model.addAttribute("expenseData", expenseData);
        model.addAttribute("vehicleExpenses", vehicleExpenses);
        model.addAttribute("chartData", chartData);
        model.addAttribute("currentMonth", monthYear);
        model.addAttribute("monthName", monthName);

        return "outsource-monthly-summary";
    }



    @GetMapping("/outsource/report-prediction/moving-average")
    public String showMovingAverageForecast(@RequestParam(required = false) Integer year,
                                            @RequestParam(required = false, defaultValue = "3") Integer n,
                                            Model model) {
        if (n < 2) n = 2;

        int currentYear = (year != null) ? year : LocalDate.now().getYear() - 1;

        List<Map<String, Object>> yearlyExpenses = new ArrayList<>();

        double maintenanceTotal = 0.0;
        double insuranceTotal = 0.0;
        double fuelTotal = 0.0;
        double repairTotal = 0.0;
        double grandTotal = 0.0;

        for (int i = n - 1; i >= 0; i--) {
            int yearToCheck = currentYear - i;

            double maintenanceSum = 0.0;
            double insuranceSum = 0.0;
            double fuelSum = 0.0;
            double repairSum = 0.0;
            double yearlyTotal = 0.0;

            for (int month = 1; month <= 12; month++) {
                LocalDate date = LocalDate.of(yearToCheck, month, 1);
                Map<String, Object> monthlyData = outsourceExpenseService.getMonthlyExpenseSummary(date);

                maintenanceSum += (double) monthlyData.getOrDefault("maintenanceCost", 0.0);
                insuranceSum += (double) monthlyData.getOrDefault("insuranceCost", 0.0);
                fuelSum += (double) monthlyData.getOrDefault("fuelCost", 0.0);
                repairSum += (double) monthlyData.getOrDefault("repairCost", 0.0);
                yearlyTotal += (double) monthlyData.getOrDefault("totalCost", 0.0);
            }

            Map<String, Object> row = new HashMap<>();
            row.put("year", yearToCheck);
            row.put("maintenanceCost", maintenanceSum);
            row.put("insuranceCost", insuranceSum);
            row.put("fuelCost", fuelSum);
            row.put("repairCost", repairSum);
            row.put("totalCost", yearlyTotal);
            yearlyExpenses.add(row);

            maintenanceTotal += maintenanceSum;
            insuranceTotal += insuranceSum;
            fuelTotal += fuelSum;
            repairTotal += repairSum;
            grandTotal += yearlyTotal;
        }

        model.addAttribute("n", n);
        model.addAttribute("yearlyExpenses", yearlyExpenses);
        model.addAttribute("movingAverage", grandTotal / n);
        model.addAttribute("maintenanceAvg", maintenanceTotal / n);
        model.addAttribute("insuranceAvg", insuranceTotal / n);
        model.addAttribute("fuelAvg", fuelTotal / n);
        model.addAttribute("repairAvg", repairTotal / n);
        model.addAttribute("grandAvg", grandTotal / n);

        return "outsource-moving-average";
    }





}