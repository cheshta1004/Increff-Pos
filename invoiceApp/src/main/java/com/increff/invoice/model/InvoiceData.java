package com.increff.invoice.model;

import com.increff.invoice.pojo.InvoicePojo;

public class InvoiceData {
    private int id;
    private int orderId;
    private String base64pdf;

    public InvoiceData() {
    }

    public InvoiceData(int id, int orderId, String base64pdf) {
        this.id = id;
        this.orderId = orderId;
        this.base64pdf = base64pdf;
    }

    public InvoiceData(InvoicePojo pojo) {
        this.id = pojo.getId();
        this.orderId = pojo.getOrderId();
        this.base64pdf = pojo.getBase64pdf();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getBase64pdf() {
        return base64pdf;
    }

    public void setBase64pdf(String base64pdf) {
        this.base64pdf = base64pdf;
    }
}