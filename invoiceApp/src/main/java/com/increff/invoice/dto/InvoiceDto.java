package com.increff.invoice.dto;

import com.increff.invoice.api.InvoiceApi;
import com.increff.invoice.pojo.InvoicePojo;
import com.increff.invoice.service.PdfService;
import com.increff.invoice.exception.ApiException;
import com.increff.invoice.model.form.DailyReportData;
import com.increff.invoice.model.form.DailyReportRequest;
import com.increff.invoice.model.form.InvoiceData;
import com.increff.invoice.model.form.SalesReportData;
import com.increff.invoice.model.form.SalesReportRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Base64;
import java.time.ZonedDateTime;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class InvoiceDto {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceDto.class);
    private static final String DAILY_REPORT_SERVICE_URL = "http://localhost:9001/pos/api/reports/daily";
    private static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    private static final DateTimeFormatter SIMPLE_DATE = DateTimeFormatter.ISO_LOCAL_DATE;

    private final InvoiceApi invoiceApi;
    private final PdfService pdfService;
    private final RestTemplate restTemplate;

    @Autowired
    public InvoiceDto(InvoiceApi invoiceApi, PdfService pdfService, RestTemplate restTemplate) {
        this.invoiceApi = invoiceApi;
        this.pdfService = pdfService;
        this.restTemplate = restTemplate;
        logger.info("InvoiceDto initialized with dependencies");
    }

    public void generateInvoice(int orderId) throws Exception {
        logger.info("Generating invoice for order ID: {}", orderId);
        
        // Generate PDF
        InvoicePojo invoicePojo = new InvoicePojo();
        invoicePojo.setOrderId(orderId);
        byte[] pdfBytes = pdfService.generateInvoicePdf(invoicePojo);
        String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);

        // Create new invoice
        invoicePojo.setCreatedAt(ZonedDateTime.now());
        invoicePojo.setFilePath("invoices/invoice_" + orderId + ".pdf");
        invoicePojo.setBase64pdf(base64Pdf);

        // Save to database
        invoiceApi.create(invoicePojo);
        logger.info("Invoice generated successfully for order ID: {}", orderId);
    }

    public InvoiceData getInvoiceByOrderId(int orderId) throws Exception {
        logger.info("Getting invoice data for order ID: {}", orderId);
        InvoicePojo pojo = invoiceApi.getByOrderId(orderId);
        return new InvoiceData(pojo);
    }

    public byte[] downloadPdf(int orderId) throws ApiException, IOException {
        logger.info("Downloading PDF for order ID: {}", orderId);
        InvoicePojo invoicePojo = new InvoicePojo();
        invoicePojo.setOrderId(orderId);
        return pdfService.generateInvoicePdf(invoicePojo);
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
