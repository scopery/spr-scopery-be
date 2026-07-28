package com.company.scopery.modules.iam.grant.application.listeners;

import com.company.scopery.modules.iam.grant.application.action.BootstrapExistingProjectsAccessAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * After PROJECT owner policy is seeded (@Order 30), backfill PROJECT IAM resources for existing projects.
 */
@Component
@Order(42)
public class ProjectIamBootstrapBackfillInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(ProjectIamBootstrapBackfillInitializer.class);

    private final BootstrapExistingProjectsAccessAction bootstrapExistingProjects;

    public ProjectIamBootstrapBackfillInitializer(BootstrapExistingProjectsAccessAction bootstrapExistingProjects) {
        this.bootstrapExistingProjects = bootstrapExistingProjects;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            int created = bootstrapExistingProjects.executeAll();
            log.info("[ProjectIamBootstrapBackfill] complete created={}", created);
        } catch (Exception ex) {
            log.warn("[ProjectIamBootstrapBackfill] failed: {}", ex.getMessage());
        }
    }
}
