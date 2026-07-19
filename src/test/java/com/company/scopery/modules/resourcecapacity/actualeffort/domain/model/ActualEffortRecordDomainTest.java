package com.company.scopery.modules.resourcecapacity.actualeffort.domain.model;
import com.company.scopery.modules.resourcecapacity.actualeffort.domain.enums.ActualEffortInputMode;
import com.company.scopery.modules.resourcecapacity.actualeffort.domain.enums.ActualEffortStatus;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal; import java.time.LocalDate; import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
class ActualEffortRecordDomainTest {
    @Test
    void recordAndCancel() {
        var r = ActualEffortRecord.record(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "TASK", UUID.randomUUID(),
                LocalDate.now(), BigDecimal.ONE, ActualEffortInputMode.TASK_PROGRESS_MANUAL, "note");
        assertEquals(ActualEffortStatus.RECORDED, r.status());
        var cancelled = r.cancel(UUID.randomUUID(), "mistake");
        assertEquals(ActualEffortStatus.CANCELLED, cancelled.status());
        assertThrows(IllegalStateException.class, () -> cancelled.cancel(UUID.randomUUID(), "again"));
    }
}
