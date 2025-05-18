package com.increff.invoice.dto;

import com.increff.invoice.api.InvoiceApi;
import com.increff.invoice.pojo.InvoicePojo;
import com.increff.invoice.service.PdfService;
import com.increff.invoice.exception.ApiException;
import com.increff.invoice.model.form.InvoiceData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Base64;
import java.time.ZonedDateTime;

@Service
public class OrderInvoiceDto {

    private static final Logger logger = LoggerFactory.getLogger(OrderInvoiceDto.class);

    private final InvoiceApi invoiceApi;
    private final PdfService pdfService;

    @Autowired
    public OrderInvoiceDto(InvoiceApi invoiceApi, PdfService pdfService) {
        this.invoiceApi = invoiceApi;
        this.pdfService = pdfService;
        logger.info("OrderInvoiceDto initialized with dependencies");
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
} 