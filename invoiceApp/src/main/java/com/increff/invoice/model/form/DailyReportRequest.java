package com.increff.invoice.model.form;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class DailyReportRequest {
    private List<DailyReportData> data;
    private DailyReportFilter filter;

    @Getter
    @Setter
    public static class DailyReportFilter {
        private String startDate;
        private String endDate;
    }
} 