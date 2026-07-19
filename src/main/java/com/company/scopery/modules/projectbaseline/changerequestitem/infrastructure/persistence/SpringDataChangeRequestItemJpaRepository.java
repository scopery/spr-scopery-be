package com.company.scopery.modules.projectbaseline.changerequestitem.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataChangeRequestItemJpaRepository extends JpaRepository<ChangeRequestItemJpaEntity, UUID> {
    List<ChangeRequestItemJpaEntity> findByChangeRequestIdOrderByCreatedAtAsc(UUID changeRequestId);
    Optional<ChangeRequestItemJpaEntity> findByIdAndChangeRequestId(UUID id, UUID changeRequestId);
}
