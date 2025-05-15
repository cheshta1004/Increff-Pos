package com.increff.invoice.dto;

import com.increff.invoice.model.form.SalesReportRequestForm;
import com.increff.invoice.service.PdfService;
import com.increff.invoice.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.ZonedDateTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class SalesReportDto {

    @Autowired
    private PdfService pdfService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public byte[] generateSalesReportPdf(SalesReportRequestForm form) throws ApiException {
        ZonedDateTime startDate = ZonedDateTime.parse(form.getStartDate(), DATE_FORMATTER);
        ZonedDateTime endDate = ZonedDateTime.parse(form.getEndDate(), DATE_FORMATTER);
        
        return pdfService.generateSalesReportPdf(
            startDate.toLocalDateTime(),
            endDate.toLocalDateTime(),
            form.getClientName(),
            null
        );
    }
} 