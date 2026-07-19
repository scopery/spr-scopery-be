package com.company.scopery.modules.servicesupport.comment.domain.model;
import org.junit.jupiter.api.Test; import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
class SupportCommentDomainTest {
    @Test void internalCommentNotPortalVisible() {
        var c = SupportComment.internal(UUID.randomUUID(), UUID.randomUUID(), "internal rca", UUID.randomUUID());
        assertThat(c.isPortalVisible()).isFalse();
    }
    @Test void clientVisibleCommentPortalVisible() {
        var c = SupportComment.clientVisible(UUID.randomUUID(), UUID.randomUUID(), "we fixed it", UUID.randomUUID());
        assertThat(c.isPortalVisible()).isTrue();
    }
}
