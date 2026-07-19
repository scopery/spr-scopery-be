package com.company.scopery.modules.resourcecapacity.conflict.domain.model;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
class AssignmentConflictDomainTest {
    @Test
    void detectAcknowledgeResolveLifecycle() {
        var c = AssignmentConflict.detect(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "OVERLAP", "HIGH", "overlap");
        assertEquals("OPEN", c.status());
        var ack = c.acknowledge();
        assertEquals("ACKNOWLEDGED", ack.status());
        assertNotNull(ack.acknowledgedAt());
        var resolved = ack.resolve();
        assertEquals("RESOLVED", resolved.status());
        assertNotNull(resolved.resolvedAt());
    }
}
