package com.microlink.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {
    
    /**
     * GET / - Welcome page with HTML frontend
     */
    @GetMapping("/")
    public String getHomePage() {
        return "home";
    }
    
    /**
     * GET /dashboard - Real-time dashboard
     */
    @GetMapping("/dashboard")
    public String getDashboard() {
        return "dashboard";
    }
    
    /**
     * GET /metrics-page - Detailed metrics page
     */
    @GetMapping("/metrics-page")
    public String getMetricsPage() {
        return "metrics";
    }
    
    /**
     * GET /health-page - Health status page
     */
    @GetMapping("/health-page")
    public String getHealthPage() {
        return "health";
    }
    

} 