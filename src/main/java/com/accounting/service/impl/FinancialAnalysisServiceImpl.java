package com.accounting.service.impl;

import com.accounting.config.TransactionCategoryMapping;
import com.accounting.entity.Account;
import com.accounting.entity.Invoice;
import com.accounting.entity.Transaction;
import com.accounting.exception.InsufficientDataException;
import com.accounting.exception.InvalidDateRangeException;
import com.accounting.dto.response.FinancialAnalysisResponse;
import com.accounting.mapper.AccountMapper;
import com.accounting.mapper.InvoiceMapper;
import com.accounting.mapper.TransactionMapper;
import com.accounting.service.FinancialAnalysisService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinancialAnalysisServiceImpl implements FinancialAnalysisService {

    private final TransactionMapper transactionMapper;
    private final AccountMapper accountMapper;
    private final InvoiceMapper invoiceMapper;

    @Override
    public FinancialAnalysisResponse getFinancialAnalysis(LocalDate startDate, LocalDate endDate) {
        // 验证日期范围
        validateDateRange(startDate, endDate);
        
        // 获取期间内的所有交易
        List<Transaction> transactions = getTransactionsBetween(startDate, endDate);
        
        // 验证数据充足性
        validateDataSufficiency(transactions);
        
        List<Transaction> incomes = transactions.stream()
                .filter(t -> "INCOME".equals(t.getType()))
                .collect(Collectors.toList());
        List<Transaction> expenses = transactions.stream()
                .filter(t -> "EXPENSE".equals(t.getType()))
                .collect(Collectors.toList());

        BigDecimal totalIncome = sumAmount(incomes);
        BigDecimal totalExpense = sumAmount(expenses);
        BigDecimal netProfit = totalIncome.subtract(totalExpense);

        // 现金流分析
        FinancialAnalysisResponse.CashFlowAnalysis cashFlow = analyzeCashFlow(incomes, expenses);

        // 财务比率
        FinancialAnalysisResponse.FinancialRatios ratios = calculateFinancialRatios(
                totalIncome, totalExpense, netProfit);

        // 趋势分析
        FinancialAnalysisResponse.TrendAnalysis trends = analyzeTrends(startDate, endDate);

        // 计算总资产
        BigDecimal totalAssets = accountMapper.selectList(null).stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 盈利能力分析
        FinancialAnalysisResponse.ProfitabilityAnalysis profitability = analyzeProfitability(
                totalIncome, totalExpense, netProfit, totalAssets);

        // 预测数据
        FinancialAnalysisResponse.ForecastData forecast = generateForecast(startDate, endDate);
        
        // 综合评估
        FinancialAnalysisResponse.FinancialSummary summary = generateFinancialSummary(
                cashFlow, ratios, trends, profitability);

        return FinancialAnalysisResponse.builder()
                .cashFlow(cashFlow)
                .ratios(ratios)
                .trends(trends)
                .profitability(profitability)
                .forecast(forecast)
                .summary(summary)
                .build();
    }

    @Override
    public FinancialAnalysisResponse getCurrentMonthAnalysis() {
        LocalDate now = LocalDate.now();
        LocalDate monthStart = now.withDayOfMonth(1);
        LocalDate monthEnd = now.withDayOfMonth(now.lengthOfMonth());
        return getFinancialAnalysis(monthStart, monthEnd);
    }

    @Override
    public FinancialAnalysisResponse getYearAnalysis(int year) {
        LocalDate yearStart = LocalDate.of(year, 1, 1);
        LocalDate yearEnd = LocalDate.of(year, 12, 31);
        return getFinancialAnalysis(yearStart, yearEnd);
    }

    /**
     * 现金流分析（使用预定义分类映射）
     */
    private FinancialAnalysisResponse.CashFlowAnalysis analyzeCashFlow(
            List<Transaction> incomes, List<Transaction> expenses) {
        
        BigDecimal totalIncome = sumAmount(incomes);
        BigDecimal totalExpense = sumAmount(expenses);
        
        // 使用分类映射进行现金流分类
        BigDecimal operatingCashFlow = totalIncome;
        BigDecimal investingCashFlow = BigDecimal.ZERO;
        BigDecimal financingCashFlow = BigDecimal.ZERO;
        
        // 根据预定义映射分类支出
        for (Transaction expense : expenses) {
            String category = expense.getCategory();
            TransactionCategoryMapping.CashFlowType flowType = 
                    TransactionCategoryMapping.getCashFlowType(category);
            
            switch (flowType) {
                case INVESTING:
                    investingCashFlow = investingCashFlow.subtract(expense.getAmount());
                    break;
                case FINANCING:
                    financingCashFlow = financingCashFlow.subtract(expense.getAmount());
                    break;
                case OPERATING:
                default:
                    operatingCashFlow = operatingCashFlow.subtract(expense.getAmount());
                    break;
            }
        }
        
        BigDecimal netCashFlow = operatingCashFlow.add(investingCashFlow).add(financingCashFlow);
        
        // 现金流利润率 = 经营活动现金流 / 总收入
        BigDecimal cashFlowMargin = totalIncome.compareTo(BigDecimal.ZERO) > 0
                ? operatingCashFlow.divide(totalIncome, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        return FinancialAnalysisResponse.CashFlowAnalysis.builder()
                .operatingActivities(operatingCashFlow)
                .investingActivities(investingCashFlow)
                .financingActivities(financingCashFlow)
                .netCashFlow(netCashFlow)
                .build();
    }

    /**
     * 计算财务比率
     */
    private FinancialAnalysisResponse.FinancialRatios calculateFinancialRatios(
            BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal netProfit) {
        
        // 获取总资产
        List<Account> accounts = accountMapper.selectList(null);
        BigDecimal totalAssets = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 获取总负债（未付发票）
        LambdaQueryWrapper<Invoice> unpaidWrapper = new LambdaQueryWrapper<>();
        unpaidWrapper.in(Invoice::getStatus, "UNPAID", "PARTIAL");
        List<Invoice> unpaidInvoices = invoiceMapper.selectList(unpaidWrapper);
        BigDecimal totalLiabilities = BigDecimal.valueOf(unpaidInvoices.size() * 10000); // 简化计算
        
        // 流动资产（现金和银行存款）
        BigDecimal currentAssets = accounts.stream()
                .filter(a -> "CASH".equals(a.getType()) || "BANK".equals(a.getType()) || "ALIPAY".equals(a.getType()))
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 流动负债（假设等于总负债）
        BigDecimal currentLiabilities = totalLiabilities;
        
        // 速动资产（流动资产 - 存货，这里简化为流动资产）
        BigDecimal quickAssets = currentAssets;
        
        // 流动比率 = 流动资产 / 流动负债
        BigDecimal currentRatio = currentLiabilities.compareTo(BigDecimal.ZERO) > 0
                ? currentAssets.divide(currentLiabilities, 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        
        // 速动比率 = 速动资产 / 流动负债
        BigDecimal quickRatio = currentLiabilities.compareTo(BigDecimal.ZERO) > 0
                ? quickAssets.divide(currentLiabilities, 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        
        // 资产负债率 = 总负债 / 总资产
        BigDecimal debtToAssetRatio = totalAssets.compareTo(BigDecimal.ZERO) > 0
                ? totalLiabilities.divide(totalAssets, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
        
        // 资产回报率 (ROA) = 净利润 / 总资产
        BigDecimal returnOnAssets = totalAssets.compareTo(BigDecimal.ZERO) > 0
                ? netProfit.divide(totalAssets, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
        
        // 利润率 = 净利润 / 总收入
        BigDecimal profitMargin = totalIncome.compareTo(BigDecimal.ZERO) > 0
                ? netProfit.divide(totalIncome, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
        
        // 费用率 = 总支出 / 总收入
        BigDecimal expenseRatio = totalIncome.compareTo(BigDecimal.ZERO) > 0
                ? totalExpense.divide(totalIncome, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        return FinancialAnalysisResponse.FinancialRatios.builder()
                .currentRatio(currentRatio)
                .quickRatio(quickRatio)
                .debtToEquityRatio(debtToAssetRatio)
                .returnOnAssets(returnOnAssets)
                .assetTurnoverRatio(expenseRatio)
                .build();
    }

    /**
     * 趋势分析
     */
    private FinancialAnalysisResponse.TrendAnalysis analyzeTrends(LocalDate startDate, LocalDate endDate) {
        // 获取当前期间数据
        List<Transaction> currentTransactions = getTransactionsBetween(startDate, endDate);
        BigDecimal currentIncome = sumIncome(currentTransactions);
        BigDecimal currentExpense = sumExpense(currentTransactions);
        BigDecimal currentProfit = currentIncome.subtract(currentExpense);
        
        // 计算期间长度（天数）
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        
        // 获取上一期间数据（环比）
        LocalDate prevStartDate = startDate.minusDays(daysBetween);
        LocalDate prevEndDate = startDate.minusDays(1);
        List<Transaction> prevTransactions = getTransactionsBetween(prevStartDate, prevEndDate);
        BigDecimal prevIncome = sumIncome(prevTransactions);
        BigDecimal prevExpense = sumExpense(prevTransactions);
        BigDecimal prevProfit = prevIncome.subtract(prevExpense);
        
        // 环比增长率
        BigDecimal incomeGrowthRate = calculateGrowthRate(prevIncome, currentIncome);
        BigDecimal expenseGrowthRate = calculateGrowthRate(prevExpense, currentExpense);
        BigDecimal profitGrowthRate = calculateGrowthRate(prevProfit, currentProfit);
        
        // 获取去年同期数据（同比）
        LocalDate yearAgoStart = startDate.minusYears(1);
        LocalDate yearAgoEnd = endDate.minusYears(1);
        List<Transaction> yearAgoTransactions = getTransactionsBetween(yearAgoStart, yearAgoEnd);
        BigDecimal yearAgoIncome = sumIncome(yearAgoTransactions);
        BigDecimal yearAgoExpense = sumExpense(yearAgoTransactions);
        
        BigDecimal yearOverYearIncomeGrowth = calculateGrowthRate(yearAgoIncome, currentIncome);
        BigDecimal yearOverYearExpenseGrowth = calculateGrowthRate(yearAgoExpense, currentExpense);
        
        // 月度趋势（最近6个月）
        List<FinancialAnalysisResponse.MonthlyTrend> monthlyTrends = calculateMonthlyTrends(6);

        return FinancialAnalysisResponse.TrendAnalysis.builder()
                .monthOverMonthGrowth(incomeGrowthRate)
                .yearOverYearGrowth(yearOverYearIncomeGrowth)
                .monthlyTrends(monthlyTrends)
                .build();
    }

    /**
     * 盈利能力分析
     */
    private FinancialAnalysisResponse.ProfitabilityAnalysis analyzeProfitability(
            BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal netProfit, BigDecimal totalAssets) {
        
        // 毛利润（简化：总收入 - 直接成本，这里假设30%的支出是直接成本）
        BigDecimal directCost = totalExpense.multiply(BigDecimal.valueOf(0.3));
        BigDecimal grossProfit = totalIncome.subtract(directCost);
        
        // 毛利率
        BigDecimal grossProfitMargin = totalIncome.compareTo(BigDecimal.ZERO) > 0
                ? grossProfit.divide(totalIncome, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
        
        // 营业利润（毛利润 - 营业费用）
        BigDecimal operatingExpense = totalExpense.multiply(BigDecimal.valueOf(0.7));
        BigDecimal operatingProfit = grossProfit.subtract(operatingExpense);
        
        // 营业利润率
        BigDecimal operatingProfitMargin = totalIncome.compareTo(BigDecimal.ZERO) > 0
                ? operatingProfit.divide(totalIncome, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
        
        // 净利润
        BigDecimal netProfitValue = netProfit;
        
        // 净利率
        BigDecimal netProfitMargin = totalIncome.compareTo(BigDecimal.ZERO) > 0
                ? netProfitValue.divide(totalIncome, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
        
        // 盈亏平衡点（固定成本 / (1 - 变动成本率)）
        // 简化：假设50%是固定成本，50%是变动成本
        BigDecimal fixedCost = totalExpense.multiply(BigDecimal.valueOf(0.5));
        BigDecimal variableCostRatio = BigDecimal.valueOf(0.5);
        BigDecimal breakEvenPoint = BigDecimal.ONE.subtract(variableCostRatio).compareTo(BigDecimal.ZERO) > 0
                ? fixedCost.divide(BigDecimal.ONE.subtract(variableCostRatio), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        
        // 资产回报率（ROA）= 净利润 / 总资产
        BigDecimal returnOnAssets = totalAssets.compareTo(BigDecimal.ZERO) > 0
                ? netProfitValue.divide(totalAssets, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        return FinancialAnalysisResponse.ProfitabilityAnalysis.builder()
                .grossProfitMargin(grossProfitMargin)
                .netProfitMargin(netProfitMargin)
                .operatingProfitMargin(operatingProfitMargin)
                .returnOnAssets(returnOnAssets)
                .breakEvenPoint(breakEvenPoint)
                .build();
    }

    /**
     * 生成预测数据（基于移动平均法）
     */
    private FinancialAnalysisResponse.ForecastData generateForecast(LocalDate startDate, LocalDate endDate) {
        // 获取最近3个月的数据用于预测
        List<FinancialAnalysisResponse.MonthlyTrend> recentMonths = calculateMonthlyTrends(3);
        
        if (recentMonths.isEmpty()) {
            return FinancialAnalysisResponse.ForecastData.builder()
                    .nextMonthRevenue(BigDecimal.ZERO)
                    .nextMonthExpense(BigDecimal.ZERO)
                    .predictedProfit(BigDecimal.ZERO)
                    .forecastMethod("移动平均法")
                    .build();
        }
        
        // 简单移动平均
        BigDecimal avgIncome = recentMonths.stream()
                .map(FinancialAnalysisResponse.MonthlyTrend::getIncome)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(recentMonths.size()), 2, RoundingMode.HALF_UP);
        
        BigDecimal avgExpense = recentMonths.stream()
                .map(FinancialAnalysisResponse.MonthlyTrend::getExpense)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(recentMonths.size()), 2, RoundingMode.HALF_UP);
        
        BigDecimal avgProfit = avgIncome.subtract(avgExpense);
        
        // 计算置信度（基于数据的稳定性，这里简化为固定值）
        BigDecimal confidenceLevel = BigDecimal.valueOf(75.0);

        return FinancialAnalysisResponse.ForecastData.builder()
                .nextMonthRevenue(avgIncome)
                .nextMonthExpense(avgExpense)
                .predictedProfit(avgProfit)
                .forecastMethod("移动平均法")
                .build();
    }

    // 辅助方法
    
    /**
     * 验证日期范围
     */
    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException(startDate, endDate);
        }
    }
    
    /**
     * 验证数据充足性
     */
    private void validateDataSufficiency(List<Transaction> transactions) {
        final int MINIMUM_REQUIRED = 3;
        if (transactions.size() < MINIMUM_REQUIRED) {
            throw new InsufficientDataException(transactions.size(), MINIMUM_REQUIRED);
        }
    }
    
    /**
     * 生成综合财务评估
     */
    private FinancialAnalysisResponse.FinancialSummary generateFinancialSummary(
            FinancialAnalysisResponse.CashFlowAnalysis cashFlow,
            FinancialAnalysisResponse.FinancialRatios ratios,
            FinancialAnalysisResponse.TrendAnalysis trends,
            FinancialAnalysisResponse.ProfitabilityAnalysis profitability) {
        
        // 计算健康评分（0-100）
        BigDecimal healthScore = calculateHealthScore(cashFlow, ratios, profitability);
        
        // 确定健康状况等级
        FinancialAnalysisResponse.HealthStatus healthStatus = determineHealthStatus(healthScore);
        
        // 生成关键洞察
        List<String> keyInsights = generateKeyInsights(cashFlow, ratios, trends, profitability);
        
        // 生成建议
        List<String> recommendations = generateRecommendations(healthStatus, ratios, profitability);
        Map<String, BigDecimal> categoryBreakdown = new HashMap<>();
        
        return FinancialAnalysisResponse.FinancialSummary.builder()
                .healthStatus(healthStatus)
                .healthScore(healthScore.intValue())
                .keyInsights(keyInsights)
                .recommendations(recommendations)
                .categoryBreakdown(categoryBreakdown)
                .build();
    }
    
    /**
     * 计算财务健康评分
     */
    private BigDecimal calculateHealthScore(
            FinancialAnalysisResponse.CashFlowAnalysis cashFlow,
            FinancialAnalysisResponse.FinancialRatios ratios,
            FinancialAnalysisResponse.ProfitabilityAnalysis profitability) {
        
        BigDecimal score = BigDecimal.ZERO;
        
        // 现金流健康度（30分）
        if (cashFlow.getNetCashFlow().compareTo(BigDecimal.ZERO) > 0) {
            score = score.add(BigDecimal.valueOf(30));
        } else if (cashFlow.getNetCashFlow().compareTo(BigDecimal.ZERO) == 0) {
            score = score.add(BigDecimal.valueOf(15));
        }
        
        // 流动比率健康度（20分）
        if (ratios.getCurrentRatio().compareTo(BigDecimal.valueOf(2)) >= 0) {
            score = score.add(BigDecimal.valueOf(20));
        } else if (ratios.getCurrentRatio().compareTo(BigDecimal.ONE) >= 0) {
            score = score.add(BigDecimal.valueOf(10));
        }
        
        // 盈利能力健康度（30分）
        if (profitability.getNetProfitMargin().compareTo(BigDecimal.valueOf(20)) >= 0) {
            score = score.add(BigDecimal.valueOf(30));
        } else if (profitability.getNetProfitMargin().compareTo(BigDecimal.valueOf(10)) >= 0) {
            score = score.add(BigDecimal.valueOf(20));
        } else if (profitability.getNetProfitMargin().compareTo(BigDecimal.ZERO) > 0) {
            score = score.add(BigDecimal.valueOf(10));
        }
        
        // 资产负债率健康度（20分）
        if (ratios.getDebtToEquityRatio().compareTo(BigDecimal.valueOf(40)) <= 0) {
            score = score.add(BigDecimal.valueOf(20));
        } else if (ratios.getDebtToEquityRatio().compareTo(BigDecimal.valueOf(60)) <= 0) {
            score = score.add(BigDecimal.valueOf(10));
        }
        
        return score;
    }
    
    /**
     * 确定健康状况等级
     */
    private FinancialAnalysisResponse.HealthStatus determineHealthStatus(BigDecimal healthScore) {
        if (healthScore.compareTo(BigDecimal.valueOf(80)) >= 0) {
            return FinancialAnalysisResponse.HealthStatus.EXCELLENT;
        } else if (healthScore.compareTo(BigDecimal.valueOf(60)) >= 0) {
            return FinancialAnalysisResponse.HealthStatus.GOOD;
        } else if (healthScore.compareTo(BigDecimal.valueOf(40)) >= 0) {
            return FinancialAnalysisResponse.HealthStatus.FAIR;
        } else {
            return FinancialAnalysisResponse.HealthStatus.POOR;
        }
    }
    
    /**
     * 生成关键洞察
     */
    private List<String> generateKeyInsights(
            FinancialAnalysisResponse.CashFlowAnalysis cashFlow,
            FinancialAnalysisResponse.FinancialRatios ratios,
            FinancialAnalysisResponse.TrendAnalysis trends,
            FinancialAnalysisResponse.ProfitabilityAnalysis profitability) {
        
        List<String> insights = new ArrayList<>();
        
        // 现金流洞察
        if (cashFlow.getNetCashFlow().compareTo(BigDecimal.ZERO) > 0) {
            insights.add(String.format("净现金流为正（%.2f），企业现金状况良好", 
                    cashFlow.getNetCashFlow()));
        } else {
            insights.add(String.format("净现金流为负（%.2f），需要关注现金流管理", 
                    cashFlow.getNetCashFlow()));
        }
        
        // 盈利能力洞察
        if (profitability.getNetProfitMargin().compareTo(BigDecimal.valueOf(15)) >= 0) {
            insights.add(String.format("净利率达到 %.2f%%，盈利能力强", 
                    profitability.getNetProfitMargin()));
        } else if (profitability.getNetProfitMargin().compareTo(BigDecimal.ZERO) < 0) {
            insights.add("当前处于亏损状态，需要优化成本结构");
        }
        
        // 增长趋势洞察
        if (trends.getMonthOverMonthGrowth().compareTo(BigDecimal.valueOf(10)) > 0) {
            insights.add(String.format("收入环比增长 %.2f%%，业务增长势头良好", 
                    trends.getMonthOverMonthGrowth()));
        } else if (trends.getMonthOverMonthGrowth().compareTo(BigDecimal.ZERO) < 0) {
            insights.add(String.format("收入环比下降 %.2f%%，需要关注业务发展", 
                    trends.getMonthOverMonthGrowth().abs()));
        }
        
        // 资产负债洞察
        if (ratios.getDebtToEquityRatio().compareTo(BigDecimal.valueOf(70)) > 0) {
            insights.add(String.format("资产负债率为 %.2f%%，财务杠杆较高", 
                    ratios.getDebtToEquityRatio()));
        }
        
        return insights;
    }
    
    /**
     * 生成建议
     */
    private List<String> generateRecommendations(
            FinancialAnalysisResponse.HealthStatus healthStatus,
            FinancialAnalysisResponse.FinancialRatios ratios,
            FinancialAnalysisResponse.ProfitabilityAnalysis profitability) {
        
        List<String> recommendations = new ArrayList<>();
        
        // 根据健康状况给出总体建议
        switch (healthStatus) {
            case POOR:
                recommendations.add("财务状况需要紧急改善，建议立即采取措施控制成本并增加收入");
                break;
            case FAIR:
                recommendations.add("财务状况一般，建议优化运营效率，提升盈利能力");
                break;
            case GOOD:
                recommendations.add("财务状况良好，建议保持当前策略并寻找增长机会");
                break;
            case EXCELLENT:
                recommendations.add("财务状况优秀，可以考虑扩大投资或业务规模");
                break;
        }
        
        // 针对性建议
        if (profitability.getNetProfitMargin().compareTo(BigDecimal.valueOf(10)) < 0) {
            recommendations.add("利润率偏低，建议审查成本结构，寻找降本增效的机会");
        }
        
        if (ratios.getCurrentRatio().compareTo(BigDecimal.ONE) < 0) {
            recommendations.add("流动比率低于1，短期偿债能力不足，建议增加流动资产或减少短期负债");
        }
        
        if (ratios.getDebtToEquityRatio().compareTo(BigDecimal.valueOf(60)) > 0) {
            recommendations.add("资产负债率较高，建议控制负债规模，优化资本结构");
        }
        
        return recommendations;
    }

    private List<Transaction> getTransactionsBetween(LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<Transaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(Transaction::getDate, startDate)
                .le(Transaction::getDate, endDate);
        return transactionMapper.selectList(wrapper);
    }

    private BigDecimal sumAmount(List<Transaction> transactions) {
        return transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumIncome(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> "INCOME".equals(t.getType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumExpense(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> "EXPENSE".equals(t.getType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateGrowthRate(BigDecimal oldValue, BigDecimal newValue) {
        if (oldValue.compareTo(BigDecimal.ZERO) == 0) {
            return newValue.compareTo(BigDecimal.ZERO) > 0 ? BigDecimal.valueOf(100) : BigDecimal.ZERO;
        }
        return newValue.subtract(oldValue)
                .divide(oldValue, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private List<FinancialAnalysisResponse.MonthlyTrend> calculateMonthlyTrends(int months) {
        List<FinancialAnalysisResponse.MonthlyTrend> trends = new ArrayList<>();
        YearMonth current = YearMonth.now();
        
        BigDecimal prevProfit = null;
        
        for (int i = months - 1; i >= 0; i--) {
            YearMonth month = current.minusMonths(i);
            LocalDate monthStart = month.atDay(1);
            LocalDate monthEnd = month.atEndOfMonth();
            
            List<Transaction> monthTransactions = getTransactionsBetween(monthStart, monthEnd);
            BigDecimal monthIncome = sumIncome(monthTransactions);
            BigDecimal monthExpense = sumExpense(monthTransactions);
            BigDecimal monthProfit = monthIncome.subtract(monthExpense);
            
            BigDecimal growthRate = prevProfit != null ? calculateGrowthRate(prevProfit, monthProfit) : BigDecimal.ZERO;
            
            trends.add(FinancialAnalysisResponse.MonthlyTrend.builder()
                    .month(month.getYear() + "-" + String.format("%02d", month.getMonthValue()))
                    .income(monthIncome)
                    .expense(monthExpense)
                    .netProfit(monthProfit)
                    .build());
            
            prevProfit = monthProfit;
        }
        
        return trends;
    }
}
