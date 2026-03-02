package com.accounting.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class FinancialAnalysisResponse {
    private CashFlowAnalysis cashFlow;
    private FinancialRatios ratios;
    private TrendAnalysis trends;
    private ProfitabilityAnalysis profitability;
    private ForecastData forecast;
    private FinancialSummary summary;

    @Data
    public static class CashFlowAnalysis {
        private BigDecimal operatingActivities;
        private BigDecimal investingActivities;
        private BigDecimal financingActivities;
        private BigDecimal netCashFlow;
    }

    @Data
    public static class FinancialRatios {
        private BigDecimal currentRatio;
        private BigDecimal quickRatio;
        private BigDecimal debtToEquityRatio;
        private BigDecimal returnOnAssets;
        private BigDecimal assetTurnoverRatio;
    }

    @Data
    public static class TrendAnalysis {
        private BigDecimal monthOverMonthGrowth;
        private BigDecimal yearOverYearGrowth;
        private List<MonthlyTrend> monthlyTrends;
    }

    @Data
    public static class MonthlyTrend {
        private String month;
        private BigDecimal income;
        private BigDecimal expense;
        private BigDecimal netProfit;
    }

    @Data
    public static class ProfitabilityAnalysis {
        private BigDecimal grossProfitMargin;
        private BigDecimal netProfitMargin;
        private BigDecimal operatingProfitMargin;
        private BigDecimal returnOnAssets;
        private BigDecimal breakEvenPoint;
    }

    @Data
    public static class ForecastData {
        private BigDecimal nextMonthRevenue;
        private BigDecimal nextMonthExpense;
        private BigDecimal predictedProfit;
        private String forecastMethod;
    }

    @Data
    public static class FinancialSummary {
        private Integer healthScore;
        private HealthStatus healthStatus;
        private List<String> keyInsights;
        private List<String> recommendations;
        private Map<String, BigDecimal> categoryBreakdown;
    }

    public enum HealthStatus {
        EXCELLENT,
        GOOD,
        FAIR,
        POOR
    }
}
