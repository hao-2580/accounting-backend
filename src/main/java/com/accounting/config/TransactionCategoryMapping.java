package com.accounting.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCategoryMapping {
    private String category;
    private CashFlowType cashFlowType;
    private boolean isInflow;
    private String description;

    public enum CashFlowType {
        OPERATING,   // 经营活动
        INVESTING,   // 投资活动
        FINANCING    // 筹资活动
    }

    // 预定义分类映射
    public static final List<TransactionCategoryMapping> CATEGORY_MAPPINGS = Arrays.asList(
            // 经营活动 - 收入
            new TransactionCategoryMapping("销售收入", CashFlowType.OPERATING, true, "主营业务收入"),
            new TransactionCategoryMapping("服务收入", CashFlowType.OPERATING, true, "服务业务收入"),
            new TransactionCategoryMapping("其他收入", CashFlowType.OPERATING, true, "其他经营收入"),
            
            // 经营活动 - 支出
            new TransactionCategoryMapping("办公租金", CashFlowType.OPERATING, false, "经营性租金支出"),
            new TransactionCategoryMapping("人员工资", CashFlowType.OPERATING, false, "员工薪酬"),
            new TransactionCategoryMapping("办公用品", CashFlowType.OPERATING, false, "日常办公支出"),
            new TransactionCategoryMapping("广告推广", CashFlowType.OPERATING, false, "市场推广费用"),
            new TransactionCategoryMapping("水电费", CashFlowType.OPERATING, false, "公用事业费用"),
            new TransactionCategoryMapping("差旅费", CashFlowType.OPERATING, false, "差旅支出"),
            
            // 投资活动 - 收入
            new TransactionCategoryMapping("投资收益", CashFlowType.INVESTING, true, "投资回报"),
            new TransactionCategoryMapping("资产处置", CashFlowType.INVESTING, true, "固定资产出售"),
            
            // 投资活动 - 支出
            new TransactionCategoryMapping("资产购置", CashFlowType.INVESTING, false, "固定资产投资"),
            new TransactionCategoryMapping("股权投资", CashFlowType.INVESTING, false, "对外投资"),
            
            // 筹资活动 - 收入
            new TransactionCategoryMapping("融资收入", CashFlowType.FINANCING, true, "融资款项"),
            new TransactionCategoryMapping("股东投资", CashFlowType.FINANCING, true, "股东注资"),
            
            // 筹资活动 - 支出
            new TransactionCategoryMapping("贷款还款", CashFlowType.FINANCING, false, "偿还借款"),
            new TransactionCategoryMapping("利息支出", CashFlowType.FINANCING, false, "融资成本"),
            new TransactionCategoryMapping("股利分配", CashFlowType.FINANCING, false, "分红支出")
    );

    /**
     * 根据类别名称获取现金流类型
     */
    public static CashFlowType getCashFlowType(String category) {
        if (category == null) {
            return CashFlowType.OPERATING; // 默认为经营活动
        }
        
        return CATEGORY_MAPPINGS.stream()
                .filter(mapping -> category.contains(mapping.getCategory()) || mapping.getCategory().contains(category))
                .findFirst()
                .map(TransactionCategoryMapping::getCashFlowType)
                .orElse(CashFlowType.OPERATING); // 默认为经营活动
    }
}
