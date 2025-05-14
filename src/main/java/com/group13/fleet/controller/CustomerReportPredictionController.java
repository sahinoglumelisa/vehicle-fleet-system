package com.group13.fleet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomerReportPredictionController {

    @GetMapping("/customer/report-prediction/monthly-summary")
    public String showMonthlyExpenseSummary(Model model) {
        return "customer-monthly-summary"; // aylık özet sayfası
    }

    @GetMapping("/customer/report-prediction/moving-average")
    public String showMovingAverageForecast(Model model) {
        return "customer-moving-average"; // tahmin sayfası
    }
}
