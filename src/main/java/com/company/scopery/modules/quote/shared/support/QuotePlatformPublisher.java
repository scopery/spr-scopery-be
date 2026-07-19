package com.company.scopery.modules.quote.shared.support;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.common.outbox.TransactionalOutboxService;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.quote.quote.domain.model.Quote;
import com.company.scopery.modules.quote.quoteversion.domain.model.QuoteVersion;
import com.company.scopery.modules.quote.shared.listeners.QuoteEventDefinitionSeedInitializer;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class QuotePlatformPublisher {

    public static final String AGGREGATE_QUOTE = "QUOTE";
    public static final String AGGREGATE_QUOTE_VERSION = "QUOTE_VERSION";

    private final TransactionalOutboxService outboxService;
    private final ImmutableAuditEventService auditEventService;

    public QuotePlatformPublisher(TransactionalOutboxService outboxService,
                                  ImmutableAuditEventService auditEventService) {
        this.outboxService = outboxService;
        this.auditEventService = auditEventService;
    }

    public void enqueueQuote(Quote quote, String eventCode) {
        outboxService.enqueue(
                AGGREGATE_QUOTE,
                quote.id(),
                eventCode,
                QuoteEventDefinitionSeedInitializer.SOURCE_SYSTEM,
                1,
                mapOf(
                        "quoteId", quote.id(),
                        "projectId", quote.projectId(),
                        "workspaceId", quote.workspaceId(),
                        "code", quote.code(),
                        "status", quote.status().name()));
    }

    public void enqueueVersion(QuoteVersion version, String eventCode) {
        outboxService.enqueue(
                AGGREGATE_QUOTE_VERSION,
                version.id(),
                eventCode,
                QuoteEventDefinitionSeedInitializer.SOURCE_SYSTEM,
                1,
                mapOf(
                        "quoteVersionId", version.id(),
                        "quoteId", version.quoteId(),
                        "projectId", version.projectId(),
                        "versionNumber", version.versionNumber(),
                        "status", version.status().name(),
                        "currencyCode", version.currencyCode()));
    }

    public void audit(AuditEventType type, UUID actorUserId, Project project, QuoteVersion version, String message) {
        auditEventService.record(type, actorUserId, "USER",
                AGGREGATE_QUOTE_VERSION, version.id(), project.organizationId(), project.workspaceId(),
                null, mapOf("projectId", project.id(), "quoteId", version.quoteId()), message);
    }

    public void auditQuote(AuditEventType type, UUID actorUserId, Project project, Quote quote, String message) {
        auditEventService.record(type, actorUserId, "USER",
                AGGREGATE_QUOTE, quote.id(), project.organizationId(), project.workspaceId(),
                null, mapOf("projectId", project.id()), message);
    }

    public static Map<String, Object> mapOf(Object... kv) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i + 1 < kv.length; i += 2) {
            map.put(String.valueOf(kv[i]), kv[i + 1]);
        }
        return map;
    }
}
