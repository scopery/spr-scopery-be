package com.company.scopery.modules.integrationhub.importjob.domain.model;
import org.junit.jupiter.api.Test; import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
class ImportJobDomainTest {
    @Test void importDryRun_success() {
        var j = ImportJob.create(UUID.randomUUID(), "DRY_RUN", "CSV", "TASK").markValidated(1,1,0).markDryRunCompleted();
        assertThat(j.status()).isEqualTo("DRY_RUN_COMPLETED");
    }
    @Test void importExecuteRequiresDryRunOrValidated() {
        var j = ImportJob.create(UUID.randomUUID(), "EXECUTE", "CSV", "TASK");
        assertThatThrownBy(() -> j.execute(1)).isInstanceOf(IllegalStateException.class);
    }
}
