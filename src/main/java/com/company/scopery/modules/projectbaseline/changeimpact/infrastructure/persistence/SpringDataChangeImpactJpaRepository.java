package com.company.scopery.modules.projectbaseline.changeimpact.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataChangeImpactJpaRepository extends JpaRepository<ChangeImpactJpaEntity, UUID> {
    Optional<ChangeImpactJpaEntity> findByChangeRequestId(UUID changeRequestId);
}
