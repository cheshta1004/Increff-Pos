package com.increff.invoice.model.form;

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