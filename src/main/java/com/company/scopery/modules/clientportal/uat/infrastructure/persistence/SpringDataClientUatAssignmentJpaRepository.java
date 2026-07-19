package com.company.scopery.modules.clientportal.uat.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataClientUatAssignmentJpaRepository extends JpaRepository<ClientUatAssignmentJpaEntity, UUID> {
    List<ClientUatAssignmentJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
