package com.company.scopery.modules.ratecard.shared.support;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.common.outbox.TransactionalOutboxService;
import com.company.scopery.modules.ratecard.shared.listeners.RateCardEventDefinitionSeedInitializer;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class RateCardPlatformPublisher {

    public static final String AGGREGATE_COST_ROLE = "COST_ROLE";
    public static final String AGGREGATE_MEMBER_COST_ROLE = "MEMBER_COST_ROLE";
    public static final String AGGREGATE_RATE_CARD = "RATE_CARD";
    public static final String AGGREGATE_RATE_CARD_VERSION = "RATE_CARD_VERSION";
    public static final String AGGREGATE_RATE_CARD_LINE = "RATE_CARD_LINE";
    public static final String AGGREGATE_INFLATION_POLICY = "INFLATION_POLICY";

    private final TransactionalOutboxService outboxService;
    private final ImmutableAuditEventService auditEventService;

    public RateCardPlatformPublisher(TransactionalOutboxService outboxService,
                                     ImmutableAuditEventService auditEventService) {
        this.outboxService = outboxService;
        this.auditEventService = auditEventService;
    }

    public void enqueue(String aggregateType, UUID aggregateId, String eventCode, Map<String, Object> payload) {
        outboxService.enqueue(
                aggregateType,
                aggregateId,
                eventCode,
                RateCardEventDefinitionSeedInitializer.SOURCE_SYSTEM,
                1,
                payload);
    }

    public void audit(AuditEventType type, UUID actorUserId, String aggregateType, UUID aggregateId,
                      UUID organizationId, UUID workspaceId, Map<String, Object> payload, String summary) {
        auditEventService.record(type, actorUserId, "USER",
                aggregateType, aggregateId, organizationId, workspaceId, null, payload, summary);
    }

    public static Map<String, Object> mapOf(Object... kv) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i + 1 < kv.length; i += 2) {
            map.put(String.valueOf(kv[i]), kv[i + 1]);
        }
        return map;
    }
}
