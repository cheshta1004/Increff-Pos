package com.increff.invoice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DailyReportData {
    private String date;
    private int orderCount;
    private int totalItems;
    private double revenue;
} 