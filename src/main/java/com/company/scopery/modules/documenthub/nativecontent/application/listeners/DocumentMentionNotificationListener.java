package com.company.scopery.modules.documenthub.nativecontent.application.listeners;

import com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPayload;
import com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class DocumentMentionNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(DocumentMentionNotificationListener.class);
    private static final String EVENT_KEY = "DOCUMENT_MENTION_EXTRACTED";

    private final EmailNotificationTriggerPublisher triggerPublisher;

    public DocumentMentionNotificationListener(EmailNotificationTriggerPublisher triggerPublisher) {
        this.triggerPublisher = triggerPublisher;
    }

    @EventListener(condition = "#event['eventCode'] == 'DOCUMENT_MENTION_EXTRACTED'")
    @Async
    public void onMentionExtracted(Map<String, Object> event) {
        UUID workspaceId = extractUuid(event, "workspaceId");
        UUID documentId = extractUuid(event, "documentId");
        Object mentionedIds = event.get("mentionedIds");

        if (documentId == null || mentionedIds == null) return;

        try {
            var payload = new EmailNotificationTriggerPayload(
                    null,
                    "DOCUMENT_HUB",
                    EVENT_KEY,
                    workspaceId,
                    null,
                    Map.of(
                            "documentId", documentId.toString(),
                            "mentionedIds", mentionedIds instanceof List<?> l ? l : List.of(mentionedIds.toString())
                    )
            );
            triggerPublisher.publish(payload);
        } catch (Exception e) {
            log.warn("Document mention notification failed for document {}: {}", documentId, e.getMessage());
        }
    }

    private UUID extractUuid(Map<String, Object> event, String key) {
        Object val = event.get(key);
        if (val == null) return null;
        try {
            return val instanceof UUID u ? u : UUID.fromString(val.toString());
        } catch (Exception e) {
            return null;
        }
    }
}
