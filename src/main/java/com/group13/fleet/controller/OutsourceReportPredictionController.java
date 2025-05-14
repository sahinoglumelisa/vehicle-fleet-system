package com.group13.fleet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OutsourceReportPredictionController {

    @GetMapping("/outsource/report-prediction/monthly-summary")
    public String showMonthlySummary(Model model) {
        return "outsource-monthly-summary"; // monthly-summary.html
    }

    @GetMapping("/outsource/report-prediction/moving-average")
    public String showMovingAverageForecast(Model model) {
        return "outsource-moving-average"; // moving-average.html
    }
}
