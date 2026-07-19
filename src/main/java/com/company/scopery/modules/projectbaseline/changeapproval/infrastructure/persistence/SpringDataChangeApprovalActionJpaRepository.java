package com.company.scopery.modules.projectbaseline.changeapproval.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataChangeApprovalActionJpaRepository extends JpaRepository<ChangeApprovalActionJpaEntity, UUID> {
    List<ChangeApprovalActionJpaEntity> findByChangeRequestIdOrderByCreatedAtAsc(UUID changeRequestId);
}
