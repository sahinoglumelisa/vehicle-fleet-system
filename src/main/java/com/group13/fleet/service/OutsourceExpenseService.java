package com.group13.fleet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class OutsourceExpenseService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> getMonthlyExpenseSummary(LocalDate targetDate) {
        String month = targetDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));

        Map<String, Object> summary = new HashMap<>();

        // Get total expenses by category for leased vehicles
        String sql = """
            SELECT 
                COALESCE(SUM(CASE WHEN s.service_type IN ('REGULAR_MAINTENANCE', 'PART_REPLACEMENT', 'TIRE_CHANGE') 
                         THEN s.cost ELSE 0 END), 0) as maintenance_cost,
                COALESCE(SUM(CASE WHEN s.service_type = 'REPAIR' 
                         THEN s.cost ELSE 0 END), 0) as repair_cost,
                COALESCE(SUM(fc.cost), 0) as fuel_cost,
                0 as insurance_cost -- Insurance tracking needs to be implemented
            FROM vehicles v
            LEFT JOIN services s ON v.vehicle_id = s.vehicle_id 
                AND DATE_TRUNC('month', s.service_date) = DATE_TRUNC('month', ?::date)
            LEFT JOIN fuel_consumption fc ON v.vehicle_id = fc.vehicle_id 
                AND DATE_TRUNC('month', fc.date) = DATE_TRUNC('month', ?::date)
            WHERE v.ownership_type = 'LEASED'
            """;

        Map<String, Object> result = jdbcTemplate.queryForMap(sql, targetDate, targetDate);

        // Safe null-checking when extracting values
        double maintenanceCost = getDoubleValue(result.get("maintenance_cost"));
        double repairCost = getDoubleValue(result.get("repair_cost"));
        double fuelCost = getDoubleValue(result.get("fuel_cost"));
        double insuranceCost = getDoubleValue(result.get("insurance_cost"));
        double totalCost = maintenanceCost + repairCost + fuelCost + insuranceCost;

        summary.put("maintenanceCost", maintenanceCost);
        summary.put("repairCost", repairCost);
        summary.put("fuelCost", fuelCost);
        summary.put("insuranceCost", insuranceCost);
        summary.put("totalCost", totalCost);

        return summary;
    }

    public List<Map<String, Object>> getVehicleExpenseBreakdown(LocalDate targetDate) {
        String sql = """
            SELECT 
                v.vehicle_id,
                v.plate_number,
                v.brand,
                v.model,
                v.year,
                COALESCE(maintenance.cost, 0) as maintenance_cost,
                COALESCE(repair.cost, 0) as repair_cost,
                COALESCE(fuel.cost, 0) as fuel_cost,
                0 as insurance_cost,
                (COALESCE(maintenance.cost, 0) + COALESCE(repair.cost, 0) + 
                 COALESCE(fuel.cost, 0) + 0) as total_cost
            FROM vehicles v
            LEFT JOIN (
                SELECT vehicle_id, SUM(cost) as cost
                FROM services
                WHERE service_type IN ('REGULAR_MAINTENANCE', 'PART_REPLACEMENT', 'TIRE_CHANGE')
                AND DATE_TRUNC('month', service_date) = DATE_TRUNC('month', ?::date)
                GROUP BY vehicle_id
            ) maintenance ON v.vehicle_id = maintenance.vehicle_id
            LEFT JOIN (
                SELECT vehicle_id, SUM(cost) as cost
                FROM services
                WHERE service_type = 'REPAIR'
                AND DATE_TRUNC('month', service_date) = DATE_TRUNC('month', ?::date)
                GROUP BY vehicle_id
            ) repair ON v.vehicle_id = repair.vehicle_id
            LEFT JOIN (
                SELECT vehicle_id, SUM(cost) as cost
                FROM fuel_consumption
                WHERE DATE_TRUNC('month', date) = DATE_TRUNC('month', ?::date)
                GROUP BY vehicle_id
            ) fuel ON v.vehicle_id = fuel.vehicle_id
            WHERE v.ownership_type = 'LEASED'
            ORDER BY total_cost DESC
            """;

        return jdbcTemplate.queryForList(sql, targetDate, targetDate, targetDate);
    }

    public List<Map<String, Object>> getExpenseChartData(LocalDate targetDate) {
        // Get last 6 months of data for chart
        List<Map<String, Object>> chartData = new ArrayList<>();

        for (int i = 5; i >= 0; i--) {
            LocalDate monthDate = targetDate.minusMonths(i);
            String monthLabel = monthDate.format(DateTimeFormatter.ofPattern("MMM yyyy"));

            String sql = """
                SELECT 
                    COALESCE(SUM(CASE WHEN s.service_type IN ('REGULAR_MAINTENANCE', 'PART_REPLACEMENT', 'TIRE_CHANGE') 
                                     THEN s.cost ELSE 0 END), 0) as maintenance,
                    COALESCE(SUM(CASE WHEN s.service_type = 'REPAIR' 
                                     THEN s.cost ELSE 0 END), 0) as repair,
                    COALESCE(SUM(fc.cost), 0) as fuel,
                    0 as insurance
                FROM vehicles v
                LEFT JOIN services s ON v.vehicle_id = s.vehicle_id 
                    AND DATE_TRUNC('month', s.service_date) = DATE_TRUNC('month', ?::date)
                LEFT JOIN fuel_consumption fc ON v.vehicle_id = fc.vehicle_id 
                    AND DATE_TRUNC('month', fc.date) = DATE_TRUNC('month', ?::date)
                WHERE v.ownership_type = 'LEASED'
                """;

            Map<String, Object> monthData = jdbcTemplate.queryForMap(sql, monthDate, monthDate);
            monthData.put("month", monthLabel);

            double total = getDoubleValue(monthData.get("maintenance")) +
                    getDoubleValue(monthData.get("repair")) +
                    getDoubleValue(monthData.get("fuel")) +
                    getDoubleValue(monthData.get("insurance"));
            monthData.put("total", total);

            chartData.add(monthData);
        }

        return chartData;
    }

    public Map<String, Object> getMovingAverageForecast() {
        // Implementation for moving average forecast
        // This would calculate trends and predictions based on historical data
        Map<String, Object> forecast = new HashMap<>();

        // Get historical data for last 12 months
        String sql = """
            SELECT 
                DATE_TRUNC('month', COALESCE(s.service_date, fc.date)) as month,
                COALESCE(SUM(COALESCE(s.cost, 0)), 0) as service_cost,
                COALESCE(SUM(COALESCE(fc.cost, 0)), 0) as fuel_cost
            FROM vehicles v
            LEFT JOIN services s ON v.vehicle_id = s.vehicle_id 
                AND s.service_date >= CURRENT_DATE - INTERVAL '12 months'
            LEFT JOIN fuel_consumption fc ON v.vehicle_id = fc.vehicle_id 
                AND fc.date >= CURRENT_DATE - INTERVAL '12 months'
            WHERE v.ownership_type = 'LEASED'
            AND (s.service_date IS NOT NULL OR fc.date IS NOT NULL)
            GROUP BY DATE_TRUNC('month', COALESCE(s.service_date, fc.date))
            ORDER BY month
            """;

        List<Map<String, Object>> historicalData = jdbcTemplate.queryForList(sql);

        // Calculate moving averages and trends
        forecast.put("historicalData", historicalData);
        forecast.put("prediction", calculatePrediction(historicalData));

        return forecast;
    }

    private Map<String, Object> calculatePrediction(List<Map<String, Object>> historicalData) {
        // Simple moving average calculation for next 3 months
        Map<String, Object> prediction = new HashMap<>();

        if (historicalData.size() >= 3) {
            double avgServiceCost = historicalData.stream()
                    .mapToDouble(data -> getDoubleValue(data.get("service_cost")))
                    .average().orElse(0.0);

            double avgFuelCost = historicalData.stream()
                    .mapToDouble(data -> getDoubleValue(data.get("fuel_cost")))
                    .average().orElse(0.0);

            prediction.put("predictedServiceCost", avgServiceCost);
            prediction.put("predictedFuelCost", avgFuelCost);
            prediction.put("predictedTotal", avgServiceCost + avgFuelCost);
        }

        return prediction;
    }

    // Helper method to safely extract double values from potentially null objects
    private double getDoubleValue(Object value) {
        if (value == null) {
            return 0.0;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return 0.0;
    }
}