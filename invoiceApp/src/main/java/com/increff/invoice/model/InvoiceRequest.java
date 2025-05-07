package com.increff.invoice.model;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class InvoiceRequest {
    private List<InvoiceItemRequest> items;
    private double totalAmount;

    @Getter
    @Setter
    public static class InvoiceItemRequest {
        private String productName;
        private Integer quantity;
        private Double sellingPrice;
        private Double totalPrice;
    }
} 