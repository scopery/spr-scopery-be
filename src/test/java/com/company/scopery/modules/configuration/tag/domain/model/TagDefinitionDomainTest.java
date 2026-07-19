package com.company.scopery.modules.configuration.tag.domain.model;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
class TagDefinitionDomainTest {
    @Test void archiveTag() {
        var t = TagDefinition.create(UUID.randomUUID(), "risk", "Risk", "#f00", null);
        assertThat(t.archive().status()).isEqualTo("ARCHIVED");
    }
}
