package com.company.scopery.modules.traceability.validationruletype.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataRegistryValidationRuleTypeJpaRepository
        extends JpaRepository<RegistryValidationRuleTypeJpaEntity, UUID> {

    @Query("SELECT r FROM RegistryValidationRuleTypeJpaEntity r WHERE r.id = :id AND (r.workspaceId = :workspaceId OR r.workspaceId IS NULL)")
    Optional<RegistryValidationRuleTypeJpaEntity> findByIdAndAccessible(@Param("id") UUID id,
                                                                          @Param("workspaceId") UUID workspaceId);

    @Query("SELECT r FROM RegistryValidationRuleTypeJpaEntity r WHERE (r.workspaceId = :workspaceId OR r.workspaceId IS NULL) ORDER BY r.displayOrder ASC")
    List<RegistryValidationRuleTypeJpaEntity> findAllAccessible(@Param("workspaceId") UUID workspaceId);

    void deleteByIdAndWorkspaceId(UUID id, UUID workspaceId);

    boolean existsByCodeAndWorkspaceIdIsNull(String code);

    boolean existsByCodeAndWorkspaceId(String code, UUID workspaceId);
}
