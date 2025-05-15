package com.increff.invoice.api;

import com.increff.invoice.dao.InvoiceDao;
import com.increff.invoice.exception.ApiException;
import com.increff.invoice.pojo.InvoicePojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.NoResultException;

@Service
@Transactional
public class InvoiceApi {
    private static final Logger logger = LoggerFactory.getLogger(InvoiceApi.class);

    @Autowired
    private InvoiceDao invoiceDao;

    public void create(InvoicePojo invoicePojo) throws ApiException {
        logger.info("Creating invoice for order ID: {}", invoicePojo.getOrderId());
        try {
            try {
                if (invoiceDao.selectByOrderId(invoicePojo.getOrderId()) != null) {
                    logger.warn("Invoice already exists for order ID: {}", invoicePojo.getOrderId());
                    throw new ApiException("Invoice already exists for order ID " + invoicePojo.getOrderId());
                }
            } catch (NoResultException e) {
                logger.debug("No existing invoice found for order ID: {}", invoicePojo.getOrderId());
            }
            invoiceDao.insert(invoicePojo);
            logger.info("Successfully created invoice for order ID: {}", invoicePojo.getOrderId());
        } catch (Exception e) {
            logger.error("Failed to create invoice for order ID: " + invoicePojo.getOrderId(), e);
            throw new ApiException("Failed to create invoice: " + e.getMessage());
        }
    }

    public void update(InvoicePojo invoicePojo) throws ApiException {
        logger.info("Updating invoice for order ID: {}", invoicePojo.getOrderId());
        try {
            try {
                InvoicePojo existingInvoice = invoiceDao.selectByOrderId(invoicePojo.getOrderId());
                invoicePojo.setId(existingInvoice.getId()); // Preserve the ID
                invoiceDao.update(invoicePojo);
                logger.info("Successfully updated invoice for order ID: {}", invoicePojo.getOrderId());
            } catch (NoResultException e) {
                logger.warn("No invoice found for order ID: {}", invoicePojo.getOrderId());
                throw new ApiException("No invoice found for order ID " + invoicePojo.getOrderId());
            }
        } catch (Exception e) {
            logger.error("Failed to update invoice for order ID: " + invoicePojo.getOrderId(), e);
            throw new ApiException("Failed to update invoice: " + e.getMessage());
        }
    }

    public InvoicePojo getByOrderId(int orderId) throws ApiException {
        logger.info("Fetching invoice for order ID: {}", orderId);
        try {
            try {
                InvoicePojo pojo = invoiceDao.selectByOrderId(orderId);
                logger.info("Successfully fetched invoice for order ID: {}", orderId);
                return pojo;
            } catch (NoResultException e) {
                logger.warn("No invoice found for order ID: {}", orderId);
                throw new ApiException("No invoice found for order ID " + orderId);
            }
        } catch (Exception e) {
            logger.error("Failed to fetch invoice for order ID: " + orderId, e);
            throw new ApiException("Failed to fetch invoice: " + e.getMessage());
        }
    }
}
