package com.company.scopery.modules.configuration.statusset.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*; import java.util.UUID;
public interface SpringDataStatusValueJpaRepository extends JpaRepository<StatusValueJpaEntity, UUID> {
    List<StatusValueJpaEntity> findByStatusSetIdOrderBySortOrderAsc(UUID setId);
}
