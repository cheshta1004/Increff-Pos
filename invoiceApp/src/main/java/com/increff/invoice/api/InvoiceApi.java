package com.increff.invoice.api;

import com.increff.invoice.dao.InvoiceDao;
import com.increff.invoice.exception.ApiException;
import com.increff.invoice.pojo.InvoicePojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InvoiceApi {

    @Autowired
    private InvoiceDao invoiceDao;

    public void create(InvoicePojo invoicePojo) throws ApiException {
        InvoicePojo existing = invoiceDao.selectByOrderId(invoicePojo.getOrderId());
        if (existing != null) {
            throw new ApiException("Invoice already exists for order ID " + invoicePojo.getOrderId());
        }
        try {
            invoiceDao.insert(invoicePojo);
        } catch (Exception e) {
            throw new ApiException("Failed to create invoice: " + e.getMessage());
        }
    }

    public InvoicePojo getByOrderId(int orderId) throws ApiException {
        try {
            InvoicePojo invoice = invoiceDao.selectByOrderId(orderId);
            if (invoice == null) {
                throw new ApiException("No invoice found for order ID " + orderId);
            }
            return invoice;
        } catch (Exception e) {
            throw new ApiException("Failed to fetch invoice: " + e.getMessage());
        }
    }
}
