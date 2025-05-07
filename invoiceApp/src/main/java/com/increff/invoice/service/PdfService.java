package com.increff.invoice.service;

import com.increff.invoice.pojo.InvoicePojo;
import com.increff.invoice.model.OrderData;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.http.ResponseEntity;
import com.increff.invoice.exception.ApiException;
import java.util.Objects;
@Service
public class PdfService {
    private static final String ORDER_SERVICE_URL = "http://localhost:9001/pos/api/order";
    private static final Logger logger = LoggerFactory.getLogger(PdfService.class);

    private final RestTemplate restTemplate;

    @Autowired
    public PdfService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        logger.info("PdfService constructor called");
    }

    public byte[] generatePdf(int orderId) throws ApiException, IOException, DocumentException {
        logger.info("Generating PDF for order ID: {}", orderId);
        
        // Get order data from order service
        OrderData orderData = getOrderData(orderId);
        if (Objects.isNull(orderData)) {
            throw new ApiException("Order not found for ID: " + orderId);
        }

        // Create PDF document
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try {
            logger.info("Creating PDF document");
            PdfWriter.getInstance(document, out);
            document.open();
            
            // Add content to PDF
            logger.info("Adding content to PDF");
            addContentToPdf(document, orderData);
            
            document.close();
            logger.info("PDF generation completed successfully");
            return out.toByteArray();
        } catch (Exception e) {
            logger.error("Error generating PDF: ", e);
            throw new ApiException("Error generating PDF: " + e.getMessage());
        }
    }

    private OrderData getOrderData(int orderId) {
        try {
            logger.info("Fetching order data from order service for order ID: {}", orderId);
            String url = ORDER_SERVICE_URL + "/get/" + orderId;
            logger.info("Calling URL: {}", url);
            
            ResponseEntity<OrderData> response = restTemplate.getForEntity(url, OrderData.class);
            OrderData orderData = response.getBody();
            
            if (Objects.isNull(orderData)) {
                logger.error("No order data received for order ID: {}", orderId);
                return null;
            }
            
            logger.info("Received order data: id={}, customerName={}, customerContact={}, items={}", 
                orderData.getId(),
                orderData.getCustomerName(), 
                orderData.getCustomerContact(),
                orderData.getItems() != null ? orderData.getItems().size() : 0);
                
            if (orderData.getItems() != null) {
                orderData.getItems().forEach(item -> 
                    logger.info("Order item: id={}, productName={}, quantity={}, price={}", 
                        item.getId(), 
                        item.getProductName(),
                        item.getQuantity(),
                        item.getSellingPrice())
                );
            }
            
            return orderData;
        } catch (Exception e) {
            logger.error("Error fetching order data: ", e);
            return null;
        }
    }

    private void addContentToPdf(Document document, OrderData orderData) throws IOException, DocumentException {
        // Add title
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("INVOICE", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Add customer details
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
        document.add(new Paragraph("Customer Name: " + (orderData.getCustomerName() != null ? orderData.getCustomerName() : "N/A"), normalFont));
        document.add(new Paragraph("Contact: " + (orderData.getCustomerContact() != null ? orderData.getCustomerContact() : "N/A"), normalFont));
        document.add(new Paragraph("Order Date: " + (orderData.getTime() != null ? orderData.getTime().toString() : "N/A"), normalFont));
        document.add(new Paragraph("\n"));

        // Add items table
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Add table headers
        Font tableHeaderFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        table.addCell(new PdfPCell(new Phrase("Item", tableHeaderFont)));
        table.addCell(new PdfPCell(new Phrase("Quantity", tableHeaderFont)));
        table.addCell(new PdfPCell(new Phrase("Price", tableHeaderFont)));
        table.addCell(new PdfPCell(new Phrase("Total", tableHeaderFont)));

        // Add table data
        if (orderData.getItems() != null) {
            orderData.getItems().forEach(item -> {
                String name = item.getProductName() != null ? item.getProductName() : "Unknown Product";
                Integer quantity = item.getQuantity() != null ? item.getQuantity() : 0;
                Double price = item.getSellingPrice() != null ? item.getSellingPrice() : 0.0;
                Double total = quantity * price;

                table.addCell(name);
                table.addCell(String.valueOf(quantity));
                table.addCell(String.format("%.2f", price));
                table.addCell(String.format("%.2f", total));
            });
        }

        document.add(table);

        // Add total amount
        double totalAmount = orderData.getItems() != null ? 
            orderData.getItems().stream()
                .mapToDouble(item -> {
                    Integer quantity = item.getQuantity() != null ? item.getQuantity() : 0;
                    Double price = item.getSellingPrice() != null ? item.getSellingPrice() : 0.0;
                    return quantity * price;
                })
                .sum() : 0.0;

        Paragraph total = new Paragraph(
            String.format("Total Amount: $%.2f", totalAmount),
            new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD)
        );
        total.setAlignment(Element.ALIGN_RIGHT);
        document.add(total);
    }
} 