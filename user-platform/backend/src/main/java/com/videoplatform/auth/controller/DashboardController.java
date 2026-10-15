package com.videoplatform.auth.controller;

import com.videoplatform.auth.dto.DashboardResponse;
import com.videoplatform.auth.service.DashboardService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService dashboard;

    public DashboardController(DashboardService dashboard) {
        this.dashboard = dashboard;
    }

    @GetMapping
    public DashboardResponse get(Authentication auth) {
        return dashboard.get(auth.getName());
    }
}
