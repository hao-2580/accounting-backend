package com.accounting.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardResponse {
    private BigDecimal monthIncome;
    private int monthIncomeCount;
    private BigDecimal monthExpense;
    private int monthExpenseCount;
    private BigDecimal monthBalance;
    private BigDecimal totalAssets;
    private BigDecimal overdueInvoiceTotal;
    private int overdueInvoiceCount;
    private List<TransactionResponse> recentTransactions;
    private List<InvoiceResponse> overdueInvoices;
}
