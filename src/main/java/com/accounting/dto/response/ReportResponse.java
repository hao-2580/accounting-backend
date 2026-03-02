package com.accounting.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ReportResponse {
    private BigDecimal yearIncome;
    private BigDecimal yearExpense;
    private BigDecimal yearBalance;
    private long yearTransactionCount;
    private List<CategoryStat> incomeByCategory;
    private List<CategoryStat> expenseByCategory;
    private List<MonthStat> monthlyStats;

    @Data
    @Builder
    public static class CategoryStat {
        private String category;
        private BigDecimal amount;
        private double percentage;
    }

    @Data
    @Builder
    public static class MonthStat {
        private int month;
        private BigDecimal income;
        private BigDecimal expense;
    }
}
