package com.increff.invoice.controller;

import com.increff.invoice.dto.SalesReportDto;
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
public class SalesReportController {

    private static final Logger logger = LoggerFactory.getLogger(SalesReportController.class);

    @Autowired
    private SalesReportDto salesReportDto;

    @RequestMapping(value = "/sales-report", method = RequestMethod.POST)
    public ResponseEntity<byte[]> generateSalesReport(@RequestBody SalesReportRequest request) throws Exception {
        logger.info("Generating sales report from {} to {}", request.getFilter().getStartDate(), request.getFilter().getEndDate());

        byte[] pdfBytes = salesReportDto.generateSalesReportPdf(request);

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