package com.company.scopery.modules.configuration.fieldoption.domain.model;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
class CustomFieldOptionDomainTest {
    @Test void archiveOption() {
        var o = CustomFieldOption.create(UUID.randomUUID(), "A", "Alpha", 1);
        assertThat(o.archive().status()).isEqualTo("ARCHIVED");
    }
}
