package com.increff.invoice.model.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesReportRequestForm {
    private String startDate;
    private String endDate;
    private String clientName;
} 