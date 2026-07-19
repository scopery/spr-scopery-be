package com.company.scopery.modules.servicesupport.sla.domain.service;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;
class SlaClockServiceTest {
    @Test
    void dueBreachAndAtRisk() {
        Instant start = Instant.parse("2026-01-01T00:00:00Z");
        Instant due = SlaClockService.dueAt(start, 60);
        assertEquals(Instant.parse("2026-01-01T01:00:00Z"), due);
        assertTrue(SlaClockService.isBreached(due, Instant.parse("2026-01-01T01:01:00Z")));
        assertFalse(SlaClockService.isBreached(due, Instant.parse("2026-01-01T00:30:00Z")));
        assertTrue(SlaClockService.isAtRisk(due, Instant.parse("2026-01-01T00:45:00Z"), 30));
    }
}
