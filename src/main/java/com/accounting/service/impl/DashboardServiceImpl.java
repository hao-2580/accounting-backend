package com.accounting.service.impl;

import com.accounting.dto.response.DashboardResponse;
import com.accounting.dto.response.InvoiceResponse;
import com.accounting.dto.response.ReportResponse;
import com.accounting.dto.response.TransactionResponse;
import com.accounting.entity.Account;
import com.accounting.entity.Invoice;
import com.accounting.entity.Transaction;
import com.accounting.mapper.AccountMapper;
import com.accounting.mapper.InvoiceMapper;
import com.accounting.mapper.TransactionMapper;
import com.accounting.service.DashboardService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final TransactionMapper transactionMapper;
    private final AccountMapper accountMapper;
    private final InvoiceMapper invoiceMapper;

    @Override
    public DashboardResponse getDashboard() {
        LocalDate now = LocalDate.now();
        LocalDate monthStart = now.withDayOfMonth(1);
        LocalDate monthEnd = now.withDayOfMonth(now.lengthOfMonth());

        // 本月收入
        LambdaQueryWrapper<Transaction> incomeWrapper = new LambdaQueryWrapper<>();
        incomeWrapper.eq(Transaction::getType, "INCOME")
                .ge(Transaction::getDate, monthStart)
                .le(Transaction::getDate, monthEnd);
        List<Transaction> incomes = transactionMapper.selectList(incomeWrapper);
        BigDecimal monthIncome = incomes.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 本月支出
        LambdaQueryWrapper<Transaction> expenseWrapper = new LambdaQueryWrapper<>();
        expenseWrapper.eq(Transaction::getType, "EXPENSE")
                .ge(Transaction::getDate, monthStart)
                .le(Transaction::getDate, monthEnd);
        List<Transaction> expenses = transactionMapper.selectList(expenseWrapper);
        BigDecimal monthExpense = expenses.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 总资产
        List<Account> accounts = accountMapper.selectList(null);
        BigDecimal totalAssets = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 逾期发票 (status is UNPAID and dueDate is before today)
        LambdaQueryWrapper<Invoice> overdueWrapper = new LambdaQueryWrapper<>();
        overdueWrapper.eq(Invoice::getStatus, "UNPAID")
                .lt(Invoice::getDueDate, LocalDate.now());
        List<Invoice> overdueInvoices = invoiceMapper.selectList(overdueWrapper);
        // Note: Invoice entity doesn't have totalAmount, would need to calculate from items
        BigDecimal overdueTotal = BigDecimal.ZERO;

        // 最近交易
        LambdaQueryWrapper<Transaction> recentWrapper = new LambdaQueryWrapper<>();
        recentWrapper.orderByDesc(Transaction::getDate, Transaction::getId)
                .last("LIMIT 5");
        List<Transaction> recentTxs = transactionMapper.selectList(recentWrapper);

        return DashboardResponse.builder()
                .monthIncome(monthIncome)
                .monthIncomeCount(incomes.size())
                .monthExpense(monthExpense)
                .monthExpenseCount(expenses.size())
                .monthBalance(monthIncome.subtract(monthExpense))
                .totalAssets(totalAssets)
                .overdueInvoiceTotal(overdueTotal)
                .overdueInvoiceCount(overdueInvoices.size())
                .recentTransactions(recentTxs.stream().map(this::toTransactionResponse).collect(Collectors.toList()))
                .overdueInvoices(overdueInvoices.stream().map(this::toInvoiceResponse).collect(Collectors.toList()))
                .build();
    }

    @Override
    public ReportResponse getReport(int year) {
        LocalDate yearStart = LocalDate.of(year, 1, 1);
        LocalDate yearEnd = LocalDate.of(year, 12, 31);

        // 年度收入
        LambdaQueryWrapper<Transaction> incomeWrapper = new LambdaQueryWrapper<>();
        incomeWrapper.eq(Transaction::getType, "INCOME")
                .ge(Transaction::getDate, yearStart)
                .le(Transaction::getDate, yearEnd);
        List<Transaction> incomes = transactionMapper.selectList(incomeWrapper);
        BigDecimal yearIncome = incomes.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 年度支出
        LambdaQueryWrapper<Transaction> expenseWrapper = new LambdaQueryWrapper<>();
        expenseWrapper.eq(Transaction::getType, "EXPENSE")
                .ge(Transaction::getDate, yearStart)
                .le(Transaction::getDate, yearEnd);
        List<Transaction> expenses = transactionMapper.selectList(expenseWrapper);
        BigDecimal yearExpense = expenses.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 按分类统计收入
        Map<String, BigDecimal> incomeByCategory = incomes.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));
        List<ReportResponse.CategoryStat> incomeStats = incomeByCategory.entrySet().stream()
                .map(e -> ReportResponse.CategoryStat.builder()
                        .category(e.getKey())
                        .amount(e.getValue())
                        .percentage(yearIncome.compareTo(BigDecimal.ZERO) > 0 
                                ? e.getValue().divide(yearIncome, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()
                                : 0.0)
                        .build())
                .sorted(Comparator.comparing(ReportResponse.CategoryStat::getAmount).reversed())
                .collect(Collectors.toList());

        // 按分类统计支出
        Map<String, BigDecimal> expenseByCategory = expenses.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));
        List<ReportResponse.CategoryStat> expenseStats = expenseByCategory.entrySet().stream()
                .map(e -> ReportResponse.CategoryStat.builder()
                        .category(e.getKey())
                        .amount(e.getValue())
                        .percentage(yearExpense.compareTo(BigDecimal.ZERO) > 0
                                ? e.getValue().divide(yearExpense, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()
                                : 0.0)
                        .build())
                .sorted(Comparator.comparing(ReportResponse.CategoryStat::getAmount).reversed())
                .collect(Collectors.toList());

        // 按月统计
        List<ReportResponse.MonthStat> monthlyStats = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            final int m = month;
            BigDecimal monthIncome = incomes.stream()
                    .filter(t -> t.getDate().getMonthValue() == m)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal monthExpense = expenses.stream()
                    .filter(t -> t.getDate().getMonthValue() == m)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            monthlyStats.add(ReportResponse.MonthStat.builder()
                    .month(month)
                    .income(monthIncome)
                    .expense(monthExpense)
                    .build());
        }

        return ReportResponse.builder()
                .yearIncome(yearIncome)
                .yearExpense(yearExpense)
                .yearBalance(yearIncome.subtract(yearExpense))
                .yearTransactionCount(incomes.size() + expenses.size())
                .incomeByCategory(incomeStats)
                .expenseByCategory(expenseStats)
                .monthlyStats(monthlyStats)
                .build();
    }

    private TransactionResponse toTransactionResponse(Transaction t) {
        Account account = accountMapper.selectById(t.getAccountId());
        TransactionResponse r = new TransactionResponse();
        r.setId(t.getId());
        r.setType(Transaction.TransactionType.valueOf(t.getType()));
        r.setAmount(t.getAmount());
        r.setCategory(t.getCategory());
        r.setDate(t.getDate());
        r.setNote(t.getNote());
        r.setAccountId(t.getAccountId());
        r.setAccountName(account != null ? account.getName() : "");
        r.setCreatedAt(t.getCreatedAt());
        return r;
    }

    private InvoiceResponse toInvoiceResponse(Invoice inv) {
        InvoiceResponse r = new InvoiceResponse();
        r.setId(inv.getId());
        r.setInvoiceNo(inv.getInvoiceNo());
        r.setClientName(inv.getClientName());
        r.setIssueDate(inv.getIssueDate());
        r.setDueDate(inv.getDueDate());
        r.setStatus(Invoice.InvoiceStatus.valueOf(inv.getStatus()));
        r.setTaxRate(inv.getTaxRate());
        r.setRemark(inv.getRemark());
        r.setCreatedAt(inv.getCreatedAt());
        // Note: subtotal, taxAmount, totalAmount need to be calculated from invoice items
        return r;
    }
}
