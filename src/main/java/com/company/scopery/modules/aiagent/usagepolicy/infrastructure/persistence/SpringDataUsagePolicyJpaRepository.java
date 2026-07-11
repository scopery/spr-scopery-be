package com.company.scopery.modules.aiagent.usagepolicy.infrastructure.persistence;

import com.company.scopery.modules.aiagent.usagepolicy.infrastructure.persistence.entity.UsagePolicyJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface SpringDataUsagePolicyJpaRepository
        extends JpaRepository<UsagePolicyJpaEntity, UUID>,
                JpaSpecificationExecutor<UsagePolicyJpaEntity> {

    boolean existsByCode(String code);

    List<UsagePolicyJpaEntity> findByStatusAndTargetType(String status, String targetType);

    List<UsagePolicyJpaEntity> findByStatusAndTargetTypeAndTargetId(String status, String targetType, UUID targetId);
}