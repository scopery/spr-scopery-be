package com.company.scopery.modules.productivity.favorite.domain.model;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
class FavoriteItemDomainTest {
    @Test void archiveFavorite() {
        FavoriteItem f = FavoriteItem.create(UUID.randomUUID(), UUID.randomUUID(), "TASK", UUID.randomUUID(), null);
        f = f.archive();
        assertThat(f.archivedAt()).isNotNull();
    }
}
