package com.company.scopery.modules.configuration.form.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*; import java.util.UUID;
public interface SpringDataFormSubmissionJpaRepository extends JpaRepository<FormSubmissionJpaEntity, UUID> {
    Optional<FormSubmissionJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<FormSubmissionJpaEntity> findByWorkspaceIdOrderByCreatedAtDesc(UUID workspaceId);
}
