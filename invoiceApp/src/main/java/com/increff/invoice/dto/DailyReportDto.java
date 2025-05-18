package com.increff.invoice.dto;

import com.increff.invoice.service.PdfService;
import com.increff.invoice.exception.ApiException;
import com.increff.invoice.model.form.DailyReportData;
import com.increff.invoice.model.form.DailyReportRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class DailyReportDto {

    private static final Logger logger = LoggerFactory.getLogger(DailyReportDto.class);
    private static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private final PdfService pdfService;

    @Autowired
    public DailyReportDto(PdfService pdfService) {
        this.pdfService = pdfService;
        logger.info("DailyReportDto initialized with dependencies");
    }

    public byte[] generateDailyReportPdf(List<DailyReportData> reportData, DailyReportRequest.DailyReportFilter filter) throws Exception {
        logger.info("Generating daily report PDF from {} to {}", filter.getStartDate(), filter.getEndDate());
        if (reportData == null || reportData.isEmpty()) {
            throw new ApiException("No data found for the specified date range");
        }
        LocalDateTime startDate;
        LocalDateTime endDate;
        
        try {
            startDate = ZonedDateTime.parse(filter.getStartDate(), ISO_DATE_TIME).toLocalDateTime();
            endDate = ZonedDateTime.parse(filter.getEndDate(), ISO_DATE_TIME).toLocalDateTime();
        } catch (Exception e) {
            try {
                startDate = LocalDateTime.parse(filter.getStartDate() + "T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                endDate = LocalDateTime.parse(filter.getEndDate() + "T23:59:59", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (Exception ex) {
                throw new ApiException("Invalid date format. Please use either ISO date-time (e.g., 2025-05-15T00:00:00Z) or simple date (e.g., 2025-05-15)");
            }
        }
        return pdfService.generateDailyReportPdf(startDate, endDate, reportData.toArray(new DailyReportData[0]));
    }
} 