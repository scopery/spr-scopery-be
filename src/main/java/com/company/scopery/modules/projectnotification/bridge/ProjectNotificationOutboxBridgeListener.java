package com.company.scopery.modules.projectnotification.bridge;

import com.company.scopery.common.outbox.PlatformOutboxPublishedEvent;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventDefinitionCode;
import com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPayload;
import com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPublisher;
import com.company.scopery.modules.projectnotification.shared.constant.ProjectNotificationEventCodes;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Bridges Phase 04 platform outbox publications into Phase 06 notification dispatch
 * for whitelisted project-related event codes.
 */
@Component
public class ProjectNotificationOutboxBridgeListener {

    private static final Logger log = LoggerFactory.getLogger(ProjectNotificationOutboxBridgeListener.class);

    private final EventDefinitionRepository eventDefinitionRepository;
    private final EmailNotificationTriggerPublisher notificationPublisher;
    private final ObjectMapper objectMapper;

    public ProjectNotificationOutboxBridgeListener(
            EventDefinitionRepository eventDefinitionRepository,
            EmailNotificationTriggerPublisher notificationPublisher,
            ObjectMapper objectMapper) {
        this.eventDefinitionRepository = eventDefinitionRepository;
        this.notificationPublisher = notificationPublisher;
        this.objectMapper = objectMapper;
    }

    @EventListener
    public void onOutboxPublished(PlatformOutboxPublishedEvent event) {
        if (event == null || event.eventType() == null) {
            return;
        }
        if (!ProjectNotificationEventCodes.BRIDGEABLE_EVENT_CODES.contains(event.eventType())) {
            return;
        }
        try {
            EventDefinition def = eventDefinitionRepository.findByCode(EventDefinitionCode.of(event.eventType()))
                    .orElse(null);
            if (def == null) {
                log.debug("[ProjectNotificationBridge] No EventDefinition for {}", event.eventType());
                return;
            }
            Map<String, Object> payload = parsePayload(event.payloadJson());
            payload.putIfAbsent("eventCode", event.eventType());
            payload.putIfAbsent("aggregateId", event.aggregateId() == null ? null : event.aggregateId().toString());
            payload.putIfAbsent("occurrenceId", event.outboxId() == null ? null : event.outboxId().toString());
            if (event.traceId() != null) {
                payload.putIfAbsent("traceId", event.traceId());
            }

            UUID workspaceId = asUuid(nested(payload, "workspace", "id"));
            UUID actorUserId = asUuid(nested(payload, "actor", "userId"));

            notificationPublisher.publish(new EmailNotificationTriggerPayload(
                    def.id(),
                    event.sourceSystem(),
                    event.eventType(),
                    workspaceId,
                    actorUserId,
                    payload
            ));
            log.info("[ProjectNotificationBridge] Triggered notification for {}", event.eventType());
        } catch (Exception ex) {
            log.error("[ProjectNotificationBridge] Failed for {}: {}", event.eventType(), ex.getMessage());
        }
    }

    private Map<String, Object> parsePayload(String json) {
        if (json == null || json.isBlank()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    @SuppressWarnings("unchecked")
    private static Object nested(Map<String, Object> payload, String parent, String child) {
        Object val = payload.get(parent);
        if (val instanceof Map<?, ?> map) {
            return map.get(child);
        }
        return payload.get(parent + "." + child);
    }

    private static UUID asUuid(Object value) {
        if (value == null) return null;
        if (value instanceof UUID uuid) return uuid;
        if (value instanceof String s && !s.isBlank()) {
            try { return UUID.fromString(s.trim()); } catch (IllegalArgumentException e) { return null; }
        }
        return null;
    }
}
