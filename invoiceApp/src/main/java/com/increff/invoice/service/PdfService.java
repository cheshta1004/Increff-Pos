package com.increff.invoice.service;

import com.increff.invoice.pojo.InvoicePojo;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.http.ResponseEntity;
import com.increff.invoice.exception.ApiException;
import com.increff.invoice.model.form.DailyReportData;
import com.increff.invoice.model.form.OrderData;
import com.increff.invoice.model.form.SalesReportData;

@Service
public class PdfService {
    private static final String ORDER_SERVICE_URL = "http://localhost:9001/pos/api/order/get";
    private static final String DAILY_REPORT_SERVICE_URL = "http://localhost:9001/pos/api/reports/daily";
    private static final Logger logger = LoggerFactory.getLogger(PdfService.class);

    @Autowired
    private RestTemplate restTemplate;

    public byte[] generateInvoicePdf(InvoicePojo invoicePojo) throws ApiException {
        try {

            ResponseEntity<OrderData> response = restTemplate.getForEntity(
                    ORDER_SERVICE_URL + "/" + invoicePojo.getOrderId(),
                    OrderData.class
            );
            OrderData orderData = response.getBody();
            if (orderData == null) {
                throw new ApiException("Failed to get order data");
            }

            // Generate PDF
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Add content to PDF
            addContentToPdf(document, orderData);

            // Close document
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            logger.error("Error generating invoice PDF", e);
            throw new ApiException("Error generating invoice PDF: " + e.getMessage());
        }
    }

    public byte[] generateSalesReportPdf(LocalDateTime startDate, LocalDateTime endDate, String clientName, SalesReportData[] reportData) throws ApiException {
        try {
            if (reportData == null || reportData.length == 0) {
                throw new ApiException("No data provided for sales report");
            }

            // Generate PDF
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Add content to PDF
            addSalesReportContentToPdf(document, reportData);

            // Close document
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            logger.error("Error generating sales report PDF", e);
            throw new ApiException("Error generating sales report PDF: " + e.getMessage());
        }
    }

    public byte[] generateDailyReportPdf(LocalDateTime startDate, LocalDateTime endDate, DailyReportData[] reportData) throws ApiException {
        try {
            if (reportData == null || reportData.length == 0) {
                throw new ApiException("No data provided for daily report");
            }

            // Generate PDF
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Add content to PDF
            addDailyReportContentToPdf(document, reportData);

            // Close document
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            logger.error("Error generating daily report PDF", e);
            throw new ApiException("Error generating daily report PDF: " + e.getMessage());
        }
    }

    private void addContentToPdf(Document document, OrderData orderData) {
        // Add title
        Paragraph title = new Paragraph("INVOICE")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(20)
                .setBold();
        document.add(title);

        // Add order details
        document.add(new Paragraph("Order ID: " + orderData.getId()));
        document.add(new Paragraph("Date: " + orderData.getTime().split("T")[0]));
        document.add(new Paragraph("Customer: " + orderData.getCustomerName()));
        if (orderData.getCustomerAddress() != null && !orderData.getCustomerAddress().isEmpty()) {
            document.add(new Paragraph("Address: " + orderData.getCustomerAddress()));
        }
        if (orderData.getCustomerContact() != null && !orderData.getCustomerContact().isEmpty()) {
            document.add(new Paragraph("Contact: " + orderData.getCustomerContact()));
        }

        // Create table for items
        Table table = new Table(UnitValue.createPercentArray(new float[]{40, 20, 20, 20}));
        table.setWidth(UnitValue.createPercentValue(100));

        // Add headers
        String[] headers = {"Item", "Quantity", "Price", "Total"};
        for (String header : headers) {
            Cell cell = new Cell().add(new Paragraph(header))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBold();
            table.addHeaderCell(cell);
        }

        // Calculate total amount
        final double totalAmount = orderData.getItems().stream()
                .mapToDouble(item -> item.getQuantity() * item.getSellingPrice())
                .sum();

        // Add items
        orderData.getItems().forEach(item -> {
            double itemTotal = item.getQuantity() * item.getSellingPrice();
            table.addCell(new Cell().add(new Paragraph(item.getProductName())));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantity()))));
            table.addCell(new Cell().add(new Paragraph(String.format("%.2f", item.getSellingPrice()))));
            table.addCell(new Cell().add(new Paragraph(String.format("%.2f", itemTotal))));
        });

        document.add(table);

        // Add total
        document.add(new Paragraph("Total Amount: " + String.format("%.2f", totalAmount))
                .setBold()
                .setTextAlignment(TextAlignment.RIGHT));
    }

    private void addSalesReportContentToPdf(Document document, SalesReportData[] reportData) {
        // Add title
        Paragraph title = new Paragraph("SALES REPORT")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(20)
                .setBold();
        document.add(title);

        // Create table for report data
        Table table = new Table(UnitValue.createPercentArray(new float[]{25, 25, 20, 15, 15}));
        table.setWidth(UnitValue.createPercentValue(100));

        // Add headers
        String[] headers = {"Client Name", "Product Name", "Barcode", "Quantity", "Revenue"};
        for (String header : headers) {
            Cell cell = new Cell().add(new Paragraph(header))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBold();
            table.addHeaderCell(cell);
        }

        // Add data rows
        for (SalesReportData data : reportData) {
            table.addCell(new Cell().add(new Paragraph(data.getClientName())));
            table.addCell(new Cell().add(new Paragraph(data.getProductName())));
            table.addCell(new Cell().add(new Paragraph(data.getBarcode())));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(data.getQuantity()))));
            table.addCell(new Cell().add(new Paragraph(String.format("%.2f", data.getRevenue()))));
        }

        document.add(table);
    }

    private void addDailyReportContentToPdf(Document document, DailyReportData[] reportData) {
        // Add title
        Paragraph title = new Paragraph("DAILY REPORT")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(20)
                .setBold();
        document.add(title);

        // Create table for report data
        Table table = new Table(UnitValue.createPercentArray(new float[]{25, 25, 20, 15, 15}));
        table.setWidth(UnitValue.createPercentValue(100));

        // Add headers
        String[] headers = {"Date", "Invoiced Orders", "Invoiced Items", "Total Revenue"};
        for (String header : headers) {
            Cell cell = new Cell().add(new Paragraph(header))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBold();
            table.addHeaderCell(cell);
        }

        // Add data rows
        for (DailyReportData data : reportData) {
            table.addCell(new Cell().add(new Paragraph(data.getDate().split("T")[0])));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(data.getOrderCount()))));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(data.getTotalItems()))));
            table.addCell(new Cell().add(new Paragraph(String.format("%.2f", data.getRevenue()))));
        }

        document.add(table);
    }
} 