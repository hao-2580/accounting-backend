package com.accounting.controller;

import com.accounting.common.Result;
import com.accounting.dto.response.DashboardResponse;
import com.accounting.dto.response.ReportResponse;
import com.accounting.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public Result<DashboardResponse> dashboard() {
        return Result.success(dashboardService.getDashboard());
    }

    @GetMapping("/reports")
    public Result<ReportResponse> report(@RequestParam(defaultValue = "0") int year) {
        if (year == 0) year = LocalDate.now().getYear();
        return Result.success(dashboardService.getReport(year));
    }
}
