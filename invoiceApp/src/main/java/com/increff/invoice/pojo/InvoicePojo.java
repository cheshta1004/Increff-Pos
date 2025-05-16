package com.increff.invoice.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.Objects;

@Entity
@Table(name = "invoice")
@Getter
@Setter
public class InvoicePojo {
    private static final ZoneId UTC_ZONE = ZoneId.of("UTC");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(nullable = false, unique = true)
    private int orderId;
    
    @Column(columnDefinition = "MEDIUMTEXT")
    private String base64pdf;

    @Column(nullable = false)
    private ZonedDateTime createdAt;

    @Column(nullable = false)
    private String filePath;

    @Transient
    private List<InvoiceItemPojo> items;

    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now(UTC_ZONE);
        if (Objects.isNull(filePath)) {
            filePath = "invoices/invoice_" + orderId + ".pdf";
        }
    }
}
