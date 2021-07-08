package ca.bc.gov.hlth.hnsecure.audit.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 * Handles persistence for ESB database audits.
 * 
 */
public abstract class AbstractAuditPersistence {    

    private static final String PERSISTENCE_UNIT_HNI_ESB_AUDITS = "HNI-ESB-AUDITS";

	public <T> T insert(T record) {
        EntityManager em = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_HNI_ESB_AUDITS).createEntityManager();
        EntityTransaction et = em.getTransaction();
        
        et.begin();
        em.persist(record);
        et.commit();
        
        return record;
    }
    
}