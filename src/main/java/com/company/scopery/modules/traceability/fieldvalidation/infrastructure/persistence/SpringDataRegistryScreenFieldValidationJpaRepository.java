package com.company.scopery.modules.traceability.fieldvalidation.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataRegistryScreenFieldValidationJpaRepository
        extends JpaRepository<RegistryScreenFieldValidationJpaEntity, UUID> {

    Optional<RegistryScreenFieldValidationJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);

    List<RegistryScreenFieldValidationJpaEntity> findByFieldId(UUID fieldId);

    @Query("SELECT v FROM RegistryScreenFieldValidationJpaEntity v WHERE v.fieldId IN :fieldIds")
    List<RegistryScreenFieldValidationJpaEntity> findByFieldIdIn(@Param("fieldIds") Collection<UUID> fieldIds);

    void deleteByIdAndWorkspaceId(UUID id, UUID workspaceId);
}
