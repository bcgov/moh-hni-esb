package com.cgi.mockendpoints.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Component
public class AuditListener {

	private static final Logger logger = LoggerFactory.getLogger(AuditListener.class);

//    @EventListener
//    public void onAuditEvent(AuditApplicationEvent event) {
//        AuditEvent auditEvent = event.getAuditEvent();
//        logger.info("typeprincipal={}", auditEvent.getType(), auditEvent.getPrincipal());
//    }
//
//    @EventListener(condition = "#event.auditEvent.type == 'AUTHENTICATION_FAILURE'")
//    public void onAuthFailure(AuditApplicationEvent event) {
//        AuditEvent auditEvent = event.getAuditEvent();
//        logger.info("authenticationure, user: {}", auditEvent.getPrincipal());
//    }
//
    @EventListener
    public void auditEventHappened(AuditApplicationEvent auditApplicationEvent) {
        
        AuditEvent auditEvent = auditApplicationEvent.getAuditEvent();
        System.out.println("Principal " + auditEvent.getPrincipal() 
          + " - " + auditEvent.getType());

        WebAuthenticationDetails details = 
          (WebAuthenticationDetails) auditEvent.getData().get("details");
        System.out.println("Remote IP address: " 
          + details.getRemoteAddress());
        System.out.println("  Session Id: " + details.getSessionId());
    }
    
//    @Bean
//    public InMemoryAuditEventRepository auditEventRepository() throws Exception {
//      return new InMemoryAuditEventRepository();
//    }
}
