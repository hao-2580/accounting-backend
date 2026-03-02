package com.accounting.controller;

import com.accounting.dto.response.FinancialAnalysisResponse;
import com.accounting.common.Result;
import com.accounting.service.FinancialAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "财务分析", description = "复杂财务计算与分析")
@RestController
@RequestMapping("/api/financial-analysis")
@RequiredArgsConstructor
public class FinancialAnalysisController {

    private final FinancialAnalysisService financialAnalysisService;

    @Operation(summary = "获取综合财务分析", description = "包含现金流、财务比率、趋势分析、盈利能力和预测数据")
    @GetMapping
    public Result<FinancialAnalysisResponse> getFinancialAnalysis(
            @Parameter(description = "开始日期") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(financialAnalysisService.getFinancialAnalysis(startDate, endDate));
    }

    @Operation(summary = "获取当前月份财务分析")
    @GetMapping("/current-month")
    public Result<FinancialAnalysisResponse> getCurrentMonthAnalysis() {
        return Result.success(financialAnalysisService.getCurrentMonthAnalysis());
    }

    @Operation(summary = "获取指定年份财务分析")
    @GetMapping("/year/{year}")
    public Result<FinancialAnalysisResponse> getYearAnalysis(
            @Parameter(description = "年份") 
            @PathVariable int year) {
        return Result.success(financialAnalysisService.getYearAnalysis(year));
    }
}
