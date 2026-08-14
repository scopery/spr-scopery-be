package com.company.scopery.modules.traceability.fieldmodeconfig.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataRegistryScreenFieldModeConfigJpaRepository
        extends JpaRepository<RegistryScreenFieldModeConfigJpaEntity, UUID> {

    Optional<RegistryScreenFieldModeConfigJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);

    List<RegistryScreenFieldModeConfigJpaEntity> findByFieldId(UUID fieldId);

    @Query("SELECT c FROM RegistryScreenFieldModeConfigJpaEntity c WHERE c.fieldId IN :fieldIds")
    List<RegistryScreenFieldModeConfigJpaEntity> findByFieldIdIn(@Param("fieldIds") Collection<UUID> fieldIds);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM RegistryScreenFieldModeConfigJpaEntity c WHERE c.id IN :ids")
    int deleteByIdIn(@Param("ids") List<UUID> ids);
}
