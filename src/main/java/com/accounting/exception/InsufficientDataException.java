package com.accounting.exception;

import lombok.Getter;

@Getter
public class InsufficientDataException extends RuntimeException {
    private final String code = "INSUFFICIENT_DATA";
    private final int transactionCount;
    private final int minimumRequired;
    private final String suggestion;

    public InsufficientDataException(int transactionCount, int minimumRequired) {
        super(String.format("数据不足：所选时间范围内的交易记录少于 %d 条，无法进行有效分析", minimumRequired));
        this.transactionCount = transactionCount;
        this.minimumRequired = minimumRequired;
        this.suggestion = "请选择更长的时间范围或确保有足够的交易记录";
    }
}
