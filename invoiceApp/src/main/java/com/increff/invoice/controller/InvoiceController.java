package com.increff.invoice.controller;

import com.increff.invoice.dto.InvoiceDto;
import com.increff.invoice.model.InvoiceData;
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
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", exposedHeaders = "Content-Disposition")
public class InvoiceController {
    private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);

    @Autowired
    private InvoiceDto invoiceDto;

    @RequestMapping(value = "/generate/{orderId}", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> handleGenerateOptions() {
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/download/{orderId}", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> handleDownloadOptions() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/generate/{orderId}")
    public ResponseEntity<String> generateInvoice(@PathVariable int orderId) throws Exception {
        logger.info("Generating invoice for order ID: {}", orderId);
        invoiceDto.generateInvoice(orderId);
        return ResponseEntity.ok("Invoice generated successfully");
    }

    @GetMapping("/download/{orderId}")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable int orderId) throws Exception {
        logger.info("Downloading invoice for order ID: {}", orderId);
        byte[] pdfBytes = invoiceDto.downloadPdf(orderId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "invoice_" + orderId + ".pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<InvoiceData> getInvoice(@PathVariable int orderId) throws Exception {
        logger.info("Getting invoice data for order ID: {}", orderId);
        InvoiceData invoiceData = invoiceDto.getInvoiceByOrderId(orderId);
        return ResponseEntity.ok(invoiceData);
    }
}
