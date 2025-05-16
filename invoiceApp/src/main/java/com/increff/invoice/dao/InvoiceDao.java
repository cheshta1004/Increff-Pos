package com.increff.invoice.dao;

import com.increff.invoice.pojo.InvoicePojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.List;

@Repository
public class InvoiceDao {

    @PersistenceContext
    private EntityManager em;
    public void insert(InvoicePojo p) {
        em.persist(p);
    }
    public InvoicePojo select(int id) {
        return em.find(InvoicePojo.class, id);
    }
    public InvoicePojo selectByOrderId(int orderId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<InvoicePojo> cq = cb.createQuery(InvoicePojo.class);
        Root<InvoicePojo> root = cq.from(InvoicePojo.class);

        cq.select(root).where(cb.equal(root.get("orderId"), orderId));

        return em.createQuery(cq).getSingleResult();
    }

}
