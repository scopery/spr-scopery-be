package com.company.scopery.modules.trust.evidence.domain.model;
import org.junit.jupiter.api.Test; import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
class ComplianceEvidenceDomainTest {
    @Test void createComplianceEvidence_success() {
        var e = ComplianceEvidenceRecord.create(UUID.randomUUID(), "ACCESS_REVIEW_COMPLETED", "Q2 review", null);
        assertThat(e.status()).isEqualTo("DRAFT");
    }
    @Test void finalizeEvidence_immutable() {
        var e = ComplianceEvidenceRecord.create(UUID.randomUUID(), "RETENTION_JOB_RUN", "Retention", null).finalizeEvidence(UUID.randomUUID());
        assertThat(e.status()).isEqualTo("FINALIZED");
        assertThatThrownBy(() -> e.withTitle("x")).isInstanceOf(IllegalStateException.class);
    }
}
