package ca.bc.gov.hlth.hnsecure.audit.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 * Handles persistence for ESB database audits.
 * 
 */
public class AuditProcessor {
    
    public <T> T insert(T record) {
        EntityManager em = Persistence.createEntityManagerFactory("HNI-ESB-AUDITS").createEntityManager();
        EntityTransaction et = em.getTransaction();
        
        et.begin();
        em.persist(record);
        et.commit();
        
        return record;
    }
    
}