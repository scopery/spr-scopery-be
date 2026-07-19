package com.company.scopery.modules.quality.testrun.domain.model;
import com.company.scopery.modules.quality.testrun.domain.enums.TestRunStatus;
import com.company.scopery.modules.quality.testrun.domain.enums.TestRunType;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
class TestRunDomainTest {
    private TestRun newRun() {
        return TestRun.create(UUID.randomUUID(), UUID.randomUUID(), "Run 1", TestRunType.MANUAL, null, null, null);
    }
    @Test void create_isPlanned() {
        var run = newRun();
        assertEquals(TestRunStatus.PLANNED, run.status());
        assertNull(run.startedAt());
        assertNull(run.completedAt());
        assertNotNull(run.id());
    }
    @Test void start_success() {
        var actor = UUID.randomUUID();
        var run = newRun().start(actor);
        assertEquals(TestRunStatus.IN_PROGRESS, run.status());
        assertNotNull(run.startedAt());
        assertEquals(actor, run.executedBy());
    }
    @Test void startNonPlanned_throws() {
        var run = newRun().start(UUID.randomUUID()).cancel();
        assertThrows(IllegalStateException.class, () -> run.start(UUID.randomUUID()));
    }
    @Test void complete_success() {
        var run = newRun().start(UUID.randomUUID()).complete("{\"passed\":5}");
        assertEquals(TestRunStatus.COMPLETED, run.status());
        assertNotNull(run.completedAt());
        assertEquals("{\"passed\":5}", run.summaryJson());
    }
    @Test void completeNonInProgress_throws() {
        var run = newRun();
        assertThrows(IllegalStateException.class, () -> run.complete("{}"));
    }
    @Test void cancel_success() {
        var run = newRun().cancel();
        assertEquals(TestRunStatus.CANCELLED, run.status());
    }
    @Test void cancelInProgress_success() {
        var run = newRun().start(UUID.randomUUID()).cancel();
        assertEquals(TestRunStatus.CANCELLED, run.status());
    }
}
