package com.company.scopery.modules.integrationhub.exportjob.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ExportJobDomainTest {
    @Test
    void exportCompleteLinksAuditLog() {
        UUID auditId = UUID.randomUUID();
        var job = ExportJob.create(UUID.randomUUID(), null, "CSV", "PROJECT")
                .complete(2, "export://x", auditId);
        assertThat(job.status()).isEqualTo("COMPLETED");
        assertThat(job.exportAuditLogId()).isEqualTo(auditId);
        assertThat(job.rowCount()).isEqualTo(2);
    }
}
