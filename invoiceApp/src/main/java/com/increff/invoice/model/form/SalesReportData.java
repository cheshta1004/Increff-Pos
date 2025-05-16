package com.increff.invoice.model.form;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SalesReportData {
    private String clientName;
    private String productName;
    private String barcode;
    private Long quantity;
    private Double revenue;
} 