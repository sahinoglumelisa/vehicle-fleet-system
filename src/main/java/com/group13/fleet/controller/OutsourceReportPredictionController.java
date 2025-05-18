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
    public String showMovingAverageForecast(Model model) {
        var forecastData = outsourceExpenseService.getMovingAverageForecast();
        model.addAttribute("forecastData", forecastData);
        return "outsource-moving-average";
    }
}