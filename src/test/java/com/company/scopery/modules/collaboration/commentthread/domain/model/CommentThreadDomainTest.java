package com.company.scopery.modules.collaboration.commentthread.domain.model;
import com.company.scopery.modules.collaboration.commentthread.domain.enums.CommentThreadStatus;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
class CommentThreadDomainTest {
    @Test void resolveAndArchive() {
        CommentThread t = CommentThread.create(UUID.randomUUID(), UUID.randomUUID(), "TASK", UUID.randomUUID(), "Q", false);
        t = t.resolve(UUID.randomUUID());
        assertThat(t.status()).isEqualTo(CommentThreadStatus.RESOLVED);
        t = t.archive(UUID.randomUUID());
        assertThat(t.status()).isEqualTo(CommentThreadStatus.ARCHIVED);
    }
}
