package com.company.scopery.common.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.UUID;

public interface ImmutableAuditEventJpaRepository extends JpaRepository<ImmutableAuditEventJpaEntity, UUID>,
        JpaSpecificationExecutor<ImmutableAuditEventJpaEntity> {
}
