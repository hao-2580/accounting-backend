package com.accounting.service;

import com.accounting.dto.response.DashboardResponse;
import com.accounting.dto.response.ReportResponse;

public interface DashboardService {
    DashboardResponse getDashboard();
    ReportResponse getReport(int year);
}
