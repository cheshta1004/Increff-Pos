package com.increff.invoice.controller;

import com.increff.invoice.dto.DailyReportDto;
import com.increff.invoice.model.form.DailyReportRequest;
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
public class DailyReportController {

    private static final Logger logger = LoggerFactory.getLogger(DailyReportController.class);

    @Autowired
    private DailyReportDto dailyReportDto;

    @RequestMapping(value = "/daily-report", method = RequestMethod.POST)
    public ResponseEntity<byte[]> generateDailyReport(@RequestBody DailyReportRequest request) throws Exception {
        logger.info("Generating daily report from {} to {}", request.getFilter().getStartDate(), request.getFilter().getEndDate());
        logger.info("Received {} data points", request.getData().size());

        byte[] pdfBytes = dailyReportDto.generateDailyReportPdf(request.getData(), request.getFilter());

        String filename = String.format("daily-report-%s-to-%s.pdf", request.getFilter().getStartDate(), request.getFilter().getEndDate());
        return buildPdfResponse(pdfBytes, filename);
    }

    private ResponseEntity<byte[]> buildPdfResponse(byte[] content, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", filename);
        return ResponseEntity.ok().headers(headers).body(content);
    }
} 