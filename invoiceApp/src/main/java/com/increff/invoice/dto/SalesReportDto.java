package com.increff.invoice.dto;

import com.increff.invoice.service.PdfService;
import com.increff.invoice.exception.ApiException;
import com.increff.invoice.model.form.SalesReportData;
import com.increff.invoice.model.form.SalesReportRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class SalesReportDto {

    private static final Logger logger = LoggerFactory.getLogger(SalesReportDto.class);
    private static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private final PdfService pdfService;

    @Autowired
    public SalesReportDto(PdfService pdfService) {
        this.pdfService = pdfService;
        logger.info("SalesReportDto initialized with dependencies");
    }

    public byte[] generateSalesReportPdf(SalesReportRequest request) throws Exception {
        logger.info("Generating sales report from {} to {}", request.getFilter().getStartDate(), request.getFilter().getEndDate());
        if (request.getData() == null || request.getData().isEmpty()) {
            throw new ApiException("No data provided for sales report");
        }
        LocalDateTime startDate;
        LocalDateTime endDate;
        try {
            startDate = ZonedDateTime.parse(request.getFilter().getStartDate(), ISO_DATE_TIME).toLocalDateTime();
            endDate = ZonedDateTime.parse(request.getFilter().getEndDate(), ISO_DATE_TIME).toLocalDateTime();
        } catch (Exception e) {
            try {
                startDate = LocalDateTime.parse(request.getFilter().getStartDate() + "T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                endDate = LocalDateTime.parse(request.getFilter().getEndDate() + "T23:59:59", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (Exception ex) {
                throw new ApiException("Invalid date format. Please use either ISO date-time (e.g., 2025-05-15T00:00:00Z) or simple date (e.g., 2025-05-15)");
            }
        }
        return pdfService.generateSalesReportPdf(
            startDate, 
            endDate, 
            request.getFilter().getClientName(), 
            request.getData().toArray(new SalesReportData[0])
        );
    }
} 