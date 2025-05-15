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
import com.increff.invoice.model.DailyReportRequest;
import com.increff.invoice.model.SalesReportRequest;
import com.increff.invoice.model.form.SalesReportRequestForm;
@RestController
@RequestMapping("/api/invoice")
@CrossOrigin(originPatterns = {"http://localhost:4200"}, 
    allowedHeaders = {"Content-Type", "Authorization", "X-User-Role", "X-Requested-With", "Accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"}, 
    exposedHeaders = {"Content-Disposition"},
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
    allowCredentials = "true")
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

    @RequestMapping(value = "/daily-report", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> handleDailyReportOptions() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Origin", "http://localhost:4200");
        headers.add("Access-Control-Allow-Methods", "POST, OPTIONS");
        headers.add("Access-Control-Allow-Headers", "Content-Type, Authorization, X-User-Role, X-Requested-With, Accept, Origin, Access-Control-Request-Method, Access-Control-Request-Headers");
        headers.add("Access-Control-Allow-Credentials", "true");
        headers.add("Access-Control-Max-Age", "3600");
        headers.add("Access-Control-Expose-Headers", "Content-Disposition, Access-Control-Allow-Origin, Access-Control-Allow-Credentials");
        return ResponseEntity.ok().headers(headers).build();
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

    @PostMapping("/daily-report")
    public ResponseEntity<byte[]> generateDailyReport(@RequestBody DailyReportRequest request) throws Exception {
        logger.info("Generating daily report from {} to {}", request.getFilter().getStartDate(), request.getFilter().getEndDate());
        logger.info("Received {} data points", request.getData().size());
        
        byte[] pdfBytes = invoiceDto.generateDailyReportPdf(request.getData(), request.getFilter());
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", 
            String.format("daily-report-%s-to-%s.pdf", 
                request.getFilter().getStartDate(), 
                request.getFilter().getEndDate()));
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @RequestMapping(value = "/sales-report", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> handleSalesReportOptions() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Origin", "http://localhost:4200");
        headers.add("Access-Control-Allow-Methods", "POST, OPTIONS");
        headers.add("Access-Control-Allow-Headers", "Content-Type, Authorization, X-User-Role, X-Requested-With, Accept, Origin, Access-Control-Request-Method, Access-Control-Request-Headers");
        headers.add("Access-Control-Allow-Credentials", "true");
        headers.add("Access-Control-Max-Age", "3600");
        headers.add("Access-Control-Expose-Headers", "Content-Disposition, Access-Control-Allow-Origin, Access-Control-Allow-Credentials");
        return ResponseEntity.ok().headers(headers).build();
    }

    @PostMapping("/sales-report")
    public ResponseEntity<byte[]> generateSalesReport(@RequestBody SalesReportRequest request) throws Exception {
        logger.info("Generating sales report from {} to {}", request.getFilter().getStartDate(), request.getFilter().getEndDate());
        
        byte[] pdfBytes = invoiceDto.generateSalesReportPdf(request);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", 
            String.format("sales-report-%s-to-%s.pdf", 
                request.getFilter().getStartDate(), 
                request.getFilter().getEndDate()));
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
