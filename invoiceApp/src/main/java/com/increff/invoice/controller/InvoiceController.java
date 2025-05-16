package com.increff.invoice.controller;

import com.increff.invoice.dto.InvoiceDto;
import com.increff.invoice.model.form.DailyReportRequest;
import com.increff.invoice.model.form.InvoiceData;
import com.increff.invoice.model.form.SalesReportRequest;
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
@CrossOrigin(
        originPatterns = "http://localhost:4200",
        allowedHeaders = {"Content-Type", "Authorization", "X-User-Role", "X-Requested-With", "Accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"},
        exposedHeaders = {"Content-Disposition"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
        allowCredentials = "true"
)
public class InvoiceController {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);

    @Autowired
    private InvoiceDto invoiceDto;

    @RequestMapping(value = "/generate/{orderId}", method = RequestMethod.POST)
    public ResponseEntity<String> generateInvoice(@PathVariable int orderId) throws Exception {
        logger.info("Generating invoice for order ID: {}", orderId);
        invoiceDto.generateInvoice(orderId);
        return ResponseEntity.ok("Invoice generated successfully");
    }

    @RequestMapping(value = "/download/{orderId}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable int orderId) throws Exception {
        logger.info("Downloading invoice for order ID: {}", orderId);
        byte[] pdfBytes = invoiceDto.downloadPdf(orderId);

        return buildPdfResponse(pdfBytes, "invoice_" + orderId + ".pdf");
    }

    @RequestMapping(value = "/{orderId}", method = RequestMethod.GET)
    public ResponseEntity<InvoiceData> getInvoice(@PathVariable int orderId) throws Exception {
        logger.info("Getting invoice data for order ID: {}", orderId);
        InvoiceData invoiceData = invoiceDto.getInvoiceByOrderId(orderId);
        return ResponseEntity.ok(invoiceData);
    }

    @RequestMapping(value = "/daily-report", method = RequestMethod.POST)
    public ResponseEntity<byte[]> generateDailyReport(@RequestBody DailyReportRequest request) throws Exception {
        logger.info("Generating daily report from {} to {}", request.getFilter().getStartDate(), request.getFilter().getEndDate());
        logger.info("Received {} data points", request.getData().size());

        byte[] pdfBytes = invoiceDto.generateDailyReportPdf(request.getData(), request.getFilter());

        String filename = String.format("daily-report-%s-to-%s.pdf", request.getFilter().getStartDate(), request.getFilter().getEndDate());
        return buildPdfResponse(pdfBytes, filename);
    }

    @RequestMapping(value = "/sales-report", method = RequestMethod.POST)
    public ResponseEntity<byte[]> generateSalesReport(@RequestBody SalesReportRequest request) throws Exception {
        logger.info("Generating sales report from {} to {}", request.getFilter().getStartDate(), request.getFilter().getEndDate());

        byte[] pdfBytes = invoiceDto.generateSalesReportPdf(request);

        String filename = String.format("sales-report-%s-to-%s.pdf", request.getFilter().getStartDate(), request.getFilter().getEndDate());
        return buildPdfResponse(pdfBytes, filename);
    }

    private ResponseEntity<byte[]> buildPdfResponse(byte[] content, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", filename);
        return ResponseEntity.ok().headers(headers).body(content);
    }
}
