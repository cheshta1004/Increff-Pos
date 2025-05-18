package com.increff.invoice.controller;

import com.increff.invoice.dto.OrderInvoiceDto;
import com.increff.invoice.model.form.InvoiceData;
import com.increff.invoice.exception.ApiException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/invoice")
public class OrderInvoiceController {

    private static final Logger logger = LoggerFactory.getLogger(OrderInvoiceController.class);

    @Autowired
    private OrderInvoiceDto orderInvoiceDto;

    @RequestMapping(value = "/generate/{orderId}", method = RequestMethod.POST)
    public ResponseEntity<String> generateInvoice(@PathVariable int orderId) throws Exception {
        logger.info("Generating invoice for order ID: {}", orderId);
        orderInvoiceDto.generateInvoice(orderId);
        return ResponseEntity.ok("Invoice generated successfully");
    }

    @RequestMapping(value = "/download/{orderId}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable int orderId) throws Exception {
        logger.info("Downloading invoice for order ID: {}", orderId);
        byte[] pdfBytes = orderInvoiceDto.downloadPdf(orderId);
        return buildPdfResponse(pdfBytes, "invoice_" + orderId + ".pdf");
    }

    @RequestMapping(value = "/{orderId}", method = RequestMethod.GET)
    public ResponseEntity<InvoiceData> getInvoice(@PathVariable int orderId) throws Exception {
        logger.info("Getting invoice data for order ID: {}", orderId);
        InvoiceData invoiceData = orderInvoiceDto.getInvoiceByOrderId(orderId);
        return ResponseEntity.ok(invoiceData);
    }

    private ResponseEntity<byte[]> buildPdfResponse(byte[] content, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", filename);
        return ResponseEntity.ok().headers(headers).body(content);
    }
} 