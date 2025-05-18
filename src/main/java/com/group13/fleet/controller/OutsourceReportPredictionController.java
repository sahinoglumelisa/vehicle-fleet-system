package com.group13.fleet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.group13.fleet.service.OutsourceExpenseService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
public class OutsourceReportPredictionController {

    @Autowired
    private OutsourceExpenseService outsourceExpenseService;

    @GetMapping("/outsource/report-prediction/monthly-summary")
    public String showMonthlySummary(
            @RequestParam(required = false) String month,
            @RequestParam(required = false) Integer year,
            Model model) {

        // Default to current month if not specified
        LocalDate targetDate = LocalDate.now();
        if (month != null && year != null) {
            targetDate = LocalDate.of(year, Integer.parseInt(month), 1);
        }

        String monthYear = targetDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));

        // Get expense data for leased vehicles
        var expenseData = outsourceExpenseService.getMonthlyExpenseSummary(targetDate);
        var vehicleExpenses = outsourceExpenseService.getVehicleExpenseBreakdown(targetDate);
        var chartData = outsourceExpenseService.getExpenseChartData(targetDate);

        model.addAttribute("expenseData", expenseData);
        model.addAttribute("vehicleExpenses", vehicleExpenses);
        model.addAttribute("chartData", chartData);
        model.addAttribute("currentMonth", monthYear);
        model.addAttribute("monthName", targetDate.format(DateTimeFormatter.ofPattern("MMMM yyyy")));

        return "outsource-monthly-summary";
    }

    @GetMapping("/outsource/report-prediction/moving-average")
    public String showMovingAverageForecast(Model model) {
        var forecastData = outsourceExpenseService.getMovingAverageForecast();
        model.addAttribute("forecastData", forecastData);
        return "outsource-moving-average";
    }
}