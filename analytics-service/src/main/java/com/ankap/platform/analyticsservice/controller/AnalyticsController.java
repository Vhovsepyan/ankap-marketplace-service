package com.ankap.platform.analyticsservice.controller;

import com.ankap.platform.analyticsservice.service.AnalyticsDashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsDashboardService analyticsService;

    public AnalyticsController(AnalyticsDashboardService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping
    public Map<String, Object> getLiveStats() {
        return Map.of(
                "totalOrders", analyticsService.getTotalOrders(),
                "totalItemsSold", analyticsService.getTotalItemsSold()
        );
    }
}