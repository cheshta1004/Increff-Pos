package com.increff.invoice.dao;

import com.increff.invoice.pojo.InvoicePojo;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class InvoiceDao {

    @PersistenceContext
    private EntityManager em;

    public void insert(InvoicePojo p) {
        em.persist(p);
    }

    public void update(InvoicePojo p) {
        em.merge(p);
    }

    public InvoicePojo select(int id) {
        return em.find(InvoicePojo.class, id);
    }

    public InvoicePojo selectByOrderId(int orderId) {
        TypedQuery<InvoicePojo> query = em.createQuery(
                "SELECT p FROM InvoicePojo p WHERE p.orderId = :orderId",
                InvoicePojo.class);
        query.setParameter("orderId", orderId);
        return query.getSingleResult();
    }

    public List<InvoicePojo> selectAll() {
        TypedQuery<InvoicePojo> query = em.createQuery(
                "SELECT p FROM InvoicePojo p",
                InvoicePojo.class);
        return query.getResultList();
    }
}
