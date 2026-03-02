package com.accounting.service;

import java.time.LocalDate;
import com.accounting.dto.response.FinancialAnalysisResponse;

public interface FinancialAnalysisService {
    /**
     * 获取综合财务分析报告
     */
    FinancialAnalysisResponse getFinancialAnalysis(LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取当前月份的财务分析
     */
    FinancialAnalysisResponse getCurrentMonthAnalysis();
    
    /**
     * 获取指定年份的财务分析
     */
    FinancialAnalysisResponse getYearAnalysis(int year);
}
