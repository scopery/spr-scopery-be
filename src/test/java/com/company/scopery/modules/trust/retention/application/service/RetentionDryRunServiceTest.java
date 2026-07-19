package com.company.scopery.modules.trust.retention.application.service;
import com.company.scopery.modules.trust.legalhold.domain.model.LegalHold;
import com.company.scopery.modules.trust.legalhold.domain.model.LegalHoldRepository;
import com.company.scopery.modules.trust.retention.domain.model.RetentionPolicy;
import org.junit.jupiter.api.Test;
import java.util.List; import java.util.Optional; import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
class RetentionDryRunServiceTest {
    @Test
    void skipsCandidatesWhenLegalHoldActive() {
        UUID ws = UUID.randomUUID();
        LegalHoldRepository holds = new LegalHoldRepository() {
            public LegalHold save(LegalHold h) { return h; }
            public Optional<LegalHold> findById(UUID id) { return Optional.empty(); }
            public List<LegalHold> findByWorkspaceId(UUID workspaceId) { return List.of(); }
            public List<LegalHold> findActiveByWorkspaceId(UUID workspaceId) {
                return List.of(LegalHold.create(ws, "LITIGATION", "WORKSPACE", null, "hold"));
            }
        };
        var service = new RetentionDryRunService(holds);
        var policy = RetentionPolicy.create(ws, "P1", "Docs", "DOCUMENT", 30, "REVIEW");
        var job = service.runDryRun(ws, policy, 5);
        assertEquals("COMPLETED", job.status());
        assertEquals(5, job.candidateCount());
        assertEquals(5, job.skippedLegalHoldCount());
        assertEquals(0, job.actionedCount());
    }
}
