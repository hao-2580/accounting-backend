package com.accounting.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class DashboardVO {

    // 本月统计
    private BigDecimal monthIncome   = BigDecimal.ZERO;
    private BigDecimal monthExpense  = BigDecimal.ZERO;
    private BigDecimal monthBalance  = BigDecimal.ZERO;
    private int        monthIncomeCount;
    private int        monthExpenseCount;

    // 发票
    private BigDecimal overdueTotal  = BigDecimal.ZERO;
    private int        overdueCount;

    // 总资产
    private BigDecimal totalAssets   = BigDecimal.ZERO;

    // 月度趋势（12个月）
    private List<Map<String, Object>> monthlyTrend;
}
