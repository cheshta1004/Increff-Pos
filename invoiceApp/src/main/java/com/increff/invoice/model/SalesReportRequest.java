package com.increff.invoice.model;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class SalesReportRequest {
    private List<SalesReportData> data;
    private SalesReportFilter filter;

    @Getter
    @Setter
    public static class SalesReportFilter {
        private String startDate;
        private String endDate;
        private String clientName;
    }
} 