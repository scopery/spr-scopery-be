package com.company.scopery.modules.integrationhub.sync.domain.service;
import com.company.scopery.modules.integrationhub.sync.domain.model.SyncCursor;
import org.junit.jupiter.api.Test; import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
class SyncRunServiceTest {
    @Test void syncRunSuccess_updatesCursor() {
        var c = SyncCursor.create(UUID.randomUUID(), UUID.randomUUID(), "k");
        var u = SyncRunService.advanceCursorOnSuccess(c, "v1", true);
        assertThat(u.cursorValue()).isEqualTo("v1");
    }
    @Test void syncRunFailure_doesNotUpdateCursor() {
        var c = SyncCursor.create(UUID.randomUUID(), UUID.randomUUID(), "k");
        var u = SyncRunService.advanceCursorOnSuccess(c, "v1", false);
        assertThat(u.cursorValue()).isNull();
    }
}
