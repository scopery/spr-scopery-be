package com.company.scopery.common.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivityLogService {

    private static final Logger log = LoggerFactory.getLogger(ActivityLogService.class);

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    public void logSuccess(String moduleCode, String entityType, String entityId,
                           String action, String actorId, String actorName,
                           String message, String metadata) {
        save(moduleCode, entityType, entityId, action, ActivityStatus.SUCCESS,
             actorId, actorName, message, metadata);
    }

    public void logFailure(String moduleCode, String entityType, String entityId,
                           String action, String actorId, String actorName,
                           String message, String metadata) {
        save(moduleCode, entityType, entityId, action, ActivityStatus.FAILED,
             actorId, actorName, message, metadata);
    }

    // REQUIRES_NEW so that a failure in the business transaction does not prevent the activity log from being saved,
    // and vice-versa — an activity log save failure never propagates to the business flow.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(String moduleCode, String entityType, String entityId,
                     String action, ActivityStatus status,
                     String actorId, String actorName,
                     String message, String metadata) {
        try {
            ActivityLogJpaEntity entity = new ActivityLogJpaEntity();
            entity.setModuleCode(moduleCode);
            entity.setEntityType(entityType);
            entity.setEntityId(entityId);
            entity.setAction(action);
            entity.setStatus(status.name());
            entity.setActorId(actorId);
            entity.setActorName(actorName);
            entity.setTraceId(MDC.get("traceId"));
            entity.setMessage(message);
            entity.setMetadata(metadata);
            activityLogRepository.save(entity);
        } catch (Exception e) {
            log.warn("Failed to persist activity log [module={} action={}]: {}", moduleCode, action, e.getMessage());
        }
    }
}
