package com.accounting.exception;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class InvalidDateRangeException extends RuntimeException {
    private final String code = "INVALID_DATE_RANGE";
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String expectedFormat = "YYYY-MM-DD";

    public InvalidDateRangeException(LocalDate startDate, LocalDate endDate) {
        super("日期范围无效：开始日期必须早于结束日期");
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
